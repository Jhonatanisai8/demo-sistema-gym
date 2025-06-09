package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.PagoDTO;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.services.impl.PagoServiceImpl;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

}
