package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/cliente/perfil")
@PreAuthorize("hasRole('ROLE_CLIENTE')")
public class ClientePerfilController {

    private final UsuarioServiceImpl usuarioService;

    @GetMapping
    public String verPerfil(Model model, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioService.obtenerPorNombre(username);
        model.addAttribute("usuario", usuario);

        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.asMap().get("successMessage"));
        }
        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.asMap().get("errorMessage"));
        }

        return "cliente/perfil/perfil";
    }
}
