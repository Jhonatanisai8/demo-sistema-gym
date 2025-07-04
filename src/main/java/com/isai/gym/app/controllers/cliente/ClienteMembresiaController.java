package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.entities.Membresia;
import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.services.UsuarioService;
import com.isai.gym.app.services.impl.MembresiaClienteServiceImpl;
import com.isai.gym.app.services.impl.MembresiaServiceImpl;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente/membresias")
@RequiredArgsConstructor
public class ClienteMembresiaController {

    private final MembresiaServiceImpl membresiaService;

    private final UsuarioServiceImpl servicioUsuario;

    private final MembresiaClienteServiceImpl servicioMembresiaCliente;

    @GetMapping
    public String listarMembresiasCliente(Model modelo, Principal principal) {
        try {
            // Obtener el usuario autenticado
            Usuario clienteAutenticado = servicioUsuario
                    .buscarPorNombreUsuarioOEmail(principal.getName());

            // Obtener la membresía activa del cliente
            MembresiaCliente membresiaActiva = servicioMembresiaCliente.obtenerMembresiaActivaPorUsuario(clienteAutenticado.getId());

            // Obtener todas las membresías históricas del cliente
            List<MembresiaCliente> todasMembresias = servicioMembresiaCliente.obtenerMembresiasPorUsuario(clienteAutenticado.getId());

            // Filtra las membresías históricas (excluyendo la activa, si ya la manejaste por separado)
            List<MembresiaCliente> membresiasHistoricas = todasMembresias.stream()
                    .filter(mc -> membresiaActiva == null || !mc.getId().equals(membresiaActiva.getId()))
                    .collect(Collectors.toList());

            modelo.addAttribute("membresiaActiva", membresiaActiva);
            modelo.addAttribute("membresiasHistoricas", membresiasHistoricas);

            if (modelo.containsAttribute("mensajeExito")) {
                modelo.addAttribute("mensajeExito", modelo.asMap().get("mensajeExito"));
            }
            if (modelo.containsAttribute("mensajeError")) {
                modelo.addAttribute("mensajeError", modelo.asMap().get("mensajeError"));
            }
            return "cliente/membresias/lista";
        } catch (IllegalArgumentException e) {
            modelo.addAttribute("mensajeError", e.getMessage());
            return "redirect:/cliente/membresias";
        } catch (Exception e) {
            modelo.addAttribute("mensajeError", "Ocurrió un error al cargar tus membresías: " + e.getMessage());
            return "client/memberships/list";
        }
    }

    @GetMapping("/comprar")
    public String mostrarMembresiasCompra(Model model) {
        List<Membresia> membresiasDisponibles = membresiaService.obtenerMembresias();
        model.addAttribute("membresiasDisponibles", membresiasDisponibles);

        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.asMap().get("successMessage"));
        }
        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.asMap().get("errorMessage"));
        }
        return "cliente/membresias/tienda";
    }

    @GetMapping("/confirmar-compra")
    public String confirmarCompra(@RequestParam("id") Long idTipoMembresia, Model modelo, RedirectAttributes atributosRedireccion) {
        try {
            Membresia tipoMembresia = membresiaService.obtenerMembresiaId(idTipoMembresia).get();

            if (!tipoMembresia.getActiva()) {
                atributosRedireccion.addFlashAttribute("mensajeError", "La membresía seleccionada no está disponible para la compra.");
                return "redirect:/cliente/membresias/tienda";
            }

            modelo.addAttribute("tipoMembresia", tipoMembresia);

            // recuperamos mensajes de error de redirecciones
            if (modelo.containsAttribute("mensajeError")) {
                modelo.addAttribute("mensajeError", modelo.asMap().get("mensajeError"));
            }
            return "cliente/membresias/confirmar-compra";
        } catch (IllegalArgumentException e) {
            atributosRedireccion.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/cliente/membresias/comprar";
        } catch (Exception e) {
            atributosRedireccion.addFlashAttribute("mensajeError", "Ocurrió un error al preparar la compra: " + e.getMessage());
            return "redirect:/cliente/membresias/comprar";
        }
    }

    @PostMapping("/realizar-compra")
    public String realizarCompra(@RequestParam("idTipoMembresia") Long idTipoMembresia,
                                 @RequestParam("metodoPago") String metodoPago,
                                 Principal principal,
                                 RedirectAttributes atributosRedireccion) {
        try {
            Usuario clienteAutenticado = servicioUsuario.buscarPorNombreUsuarioOEmail(principal.getName());

            Membresia tipoMembresia = membresiaService.obtenerMembresiaId(idTipoMembresia).get();

            if (!tipoMembresia.getActiva()) {
                atributosRedireccion.addFlashAttribute("mensajeError", "Error: La membresía no está activa para la compra.");
                return "redirect:/cliente/membresias/comprar";
            }

            servicioMembresiaCliente.adquirirMembresia(clienteAutenticado, tipoMembresia, metodoPago);

            atributosRedireccion.addFlashAttribute("mensajeExito", "¡Membresía '" + tipoMembresia.getNombre() + "' adquirida exitosamente!");
            return "redirect:/cliente/membresias"; // Redirigir al listado de membresías del cliente
        } catch (IllegalArgumentException e) {
            atributosRedireccion.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/cliente/membresias/confirmar-compra?id=" + idTipoMembresia; // Volver a la confirmación con error
        } catch (Exception e) {
            atributosRedireccion.addFlashAttribute("mensajeError", "Ocurrió un error al procesar tu compra: " + e.getMessage());
            return "redirect:/cliente/membresias/confirmar-compra?id=" + idTipoMembresia;
        }
    }


}
