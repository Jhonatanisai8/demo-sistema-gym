package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.entities.AccesoGimnasio;
import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.services.impl.AccesoGimnasioServicioImpl;
import com.isai.gym.app.services.impl.MembresiaClienteServiceImpl;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cliente/dashboard")
@PreAuthorize("hasRole('ROLE_CLIENTE')")
@RequiredArgsConstructor
public class ClienteDashBoardController {

    private final UsuarioServiceImpl usuarioService;

    private final MembresiaClienteServiceImpl membresiaClienteService;

    private final AccesoGimnasioServicioImpl accesoGimnasioServicio;

    @GetMapping("")
    public String mostrarDashboard(Principal principal, Model modelo) {
        if (principal == null) {
            modelo.addAttribute("mensajeError", "No se pudo identificar al usuario. Por favor, inicia sesión.");
            return "/cliente/dashboard";
        }

        try {
            // obtenemos  los datos completos del usuario autenticado
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorNombreUsuario(principal.getName());
            if (usuarioOpt.isEmpty()) {
                modelo.addAttribute("mensajeError", "Usuario no encontrado en la base de datos.");
                return "auth/login";
            }
            Usuario usuario = usuarioOpt.get();
            modelo.addAttribute("usuario", usuario); // Añadir el objeto usuario al modelo

            //  obtenemos  la membresía activa del usuario
            Optional<MembresiaCliente> membresiaActivaOpt = membresiaClienteService.obtenerMembresiaActivaPorUsuarioId(usuario.getId());
            membresiaActivaOpt.ifPresentOrElse(
                    membresiaCliente -> modelo.addAttribute("membresiaActiva", membresiaCliente),
                    () -> modelo.addAttribute("membresiaActiva", null)
            );

            //  obtenmos los últimos accesos del usuario (últimos 5)
            List<AccesoGimnasio> ultimosAccesos = accesoGimnasioServicio.obtenerUltimosAccesosPorUsuarioId(usuario.getId(), 5);
            modelo.addAttribute("ultimosAccesos", ultimosAccesos);

        } catch (Exception e) {
            System.err.println("Error al cargar el dashboard del cliente: " + e.getMessage());
            e.printStackTrace();
            modelo.addAttribute("mensajeError", "Ocurrió un error al cargar el dashboard. Intenta de nuevo más tarde.");
            return "cliente/dashboard";
        }

        return "cliente/dashboard";
    }
}