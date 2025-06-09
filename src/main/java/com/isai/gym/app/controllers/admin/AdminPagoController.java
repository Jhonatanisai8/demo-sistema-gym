package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.PagoDTO;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.services.impl.PagoServiceImpl;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import org.springframework.validation.FieldError; // Necesitas esta importación
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/pagos")
public class AdminPagoController {

    private final PagoServiceImpl pagoService;

    private final UsuarioServiceImpl usuarioService;

    @GetMapping({"/historial", "/"})
    public String historialPagos(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PagoDTO> pagosPage = pagoService.obtenerHistorialPagos(pageable, keyword);

        model.addAttribute("pagosPage", pagosPage);
        model.addAttribute("keyword", keyword);

        int totalPages = pagosPage.getTotalPages();
        if (totalPages > 0) {
            model.addAttribute("pageNumbers", IntStream.rangeClosed(1, totalPages).boxed().toList());
        }

        return "admin/pagos/historial";
    }

    @GetMapping("/registrar")
    public String mostrarFormularioRegistro(Model model) {
        PagoDTO nuevoPago = new PagoDTO();
        nuevoPago.setFechaPago(LocalDateTime.now());
        nuevoPago.setEstado("COMPLETADO");
        model.addAttribute("pagoDTO", nuevoPago);
        model.addAttribute("metodosPago", Arrays.asList("EFECTIVO", "TARJETA_CREDITO", "TARJETA_DEBITO", "TRANSFERENCIA_BANCARIA", "PAYPAL", "OTRO"));
        model.addAttribute("estadosPago", Arrays.asList("PENDIENTE", "COMPLETADO", "REEMBOLSADO", "CANCELADO"));
        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/pagos/registrar";
    }

    @PostMapping("/registrar")
    public String registrarPago(@Valid @ModelAttribute("pagoDTO") PagoDTO pagoDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            // --- INICIO: LOGGING ADICIONAL PARA DEPURACIÓN ---
            System.out.println("--------------------------------------------------");
            System.out.println("ERRORES DE VALIDACIÓN ENCONTRADOS EN BindingResult:");
            result.getAllErrors().forEach(error -> {
                System.out.println("  - Objeto: " + error.getObjectName());
                System.out.println("    Código de error: " + error.getCode());
                System.out.println("    Mensaje por defecto: " + error.getDefaultMessage());
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    System.out.println("    Campo afectado: " + fieldError.getField());
                    System.out.println("    Valor rechazado: " + fieldError.getRejectedValue());
                }
                System.out.println("--------------------------------------------------");
            });
            System.out.println("FIN DE ERRORES DE VALIDACIÓN.");
            System.out.println("--------------------------------------------------");
            // --- FIN: LOGGING ADICIONAL PARA DEPURACIÓN ---

            model.addAttribute("errorMessage", "Por favor, corrige los errores en el formulario.");
            // Recargar listas de opciones y usuarios para que la vista se muestre correctamente con los errores
            model.addAttribute("metodosPago", Arrays.asList("EFECTIVO", "TARJETA_CREDITO", "TARJETA_DEBITO", "TRANSFERENCIA_BANCARIA", "PAYPAL", "OTRO"));
            model.addAttribute("estadosPago", Arrays.asList("PENDIENTE", "COMPLETADO", "REEMBOLSADO", "CANCELADO"));
            List<Usuario> usuarios = usuarioService.obtenerUsuarios(); // ASUMO que 'usuarioService' está correctamente inyectado
            model.addAttribute("usuarios", usuarios);

            if (pagoDTO.getUsuarioId() != null) {
                usuarioService.obtenerPorId(pagoDTO.getUsuarioId()).ifPresent(usuario ->
                        pagoDTO.setNombreUsuario(usuario.getNombreCompleto())
                );
            }
            return "admin/pagos/registrar";
        }

        try {
            pagoService.registrarPago(pagoDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Pago registrado exitosamente.");
            return "redirect:/admin/pagos/historial";
        } catch (IllegalArgumentException e) {
            // ... (resto del catch)
            model.addAttribute("errorMessage", e.getMessage()); // Asegúrate que este errorMessage se muestre en la vista
            // Recargar listas para el formulario
            model.addAttribute("metodosPago", Arrays.asList("EFECTIVO", "TARJETA_CREDITO", "TARJETA_DEBITO", "TRANSFERENCIA_BANCARIA", "PAYPAL", "OTRO"));
            model.addAttribute("estadosPago", Arrays.asList("PENDIENTE", "COMPLETADO", "REEMBOLSADO", "CANCELADO"));
            List<Usuario> usuarios = usuarioService.obtenerUsuarios();
            model.addAttribute("usuarios", usuarios);
            if (pagoDTO.getUsuarioId() != null) {
                usuarioService.obtenerPorId(pagoDTO.getUsuarioId()).ifPresent(usuario ->
                        pagoDTO.setNombreUsuario(usuario.getNombreCompleto())
                );
            }
            return "admin/pagos/registrar";
        } catch (Exception e) {
            // ... (resto del catch)
            model.addAttribute("errorMessage", "Ocurrió un error inesperado al registrar el pago: " + e.getMessage()); // Asegúrate que este errorMessage se muestre en la vista
            // Recargar listas para el formulario
            model.addAttribute("metodosPago", Arrays.asList("EFECTIVO", "TARJETA_CREDITO", "TARJETA_DEBITO", "TRANSFERENCIA_BANCARIA", "PAYPAL", "OTRO"));
            model.addAttribute("estadosPago", Arrays.asList("PENDIENTE", "COMPLETADO", "REEMBOLSADO", "CANCELADO"));
            List<Usuario> usuarios = usuarioService.obtenerUsuarios();
            model.addAttribute("usuarios", usuarios);
            if (pagoDTO.getUsuarioId() != null) {
                usuarioService.obtenerPorId(pagoDTO.getUsuarioId()).ifPresent(usuario ->
                        pagoDTO.setNombreUsuario(usuario.getNombreCompleto())
                );
            }
            return "admin/pagos/registrar";
        }
    }
}
