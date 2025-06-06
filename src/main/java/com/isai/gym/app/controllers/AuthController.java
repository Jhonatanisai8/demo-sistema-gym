package com.isai.gym.app.controllers;

import com.isai.gym.app.dtos.RegistroUsuarioDTO;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AuthController {

    private final UsuarioServiceImpl usuarioService;

    @Autowired
    public AuthController(UsuarioServiceImpl usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarPaginaLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Nombre de usuario o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Se ha cerradoSesion Correctamente.");
        }
        return "auth/login";
    }

    @GetMapping("/registro")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registroUsuarioDTO", new RegistroUsuarioDTO());
        return "auth/registro";
    }

    @PostMapping(path = "/registro")
    public String registerUser(@Valid @ModelAttribute("registroUsuarioDTO") RegistroUsuarioDTO registroDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (registroDTO.getImagen() == null || registroDTO.getImagen().isEmpty()) {
            result.rejectValue("imagen", "NotNull", "La imagen de perfil es obligatoria.");
        }
        if (result.hasErrors()) {
            return "auth/registro";
        }
        try {
            usuarioService.registrarUsuario(registroDTO);
            redirectAttributes.addFlashAttribute("successMessage", "¡Registro exitoso! Ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("nombre de usuario")) {
                result.rejectValue("nombreUsuario", null, e.getMessage());
            } else if (e.getMessage().contains("email")) {
                result.rejectValue("email", null, e.getMessage());
            } else {
                result.reject(null, e.getMessage());
            }
            return "auth/registro";
        } catch (Exception e) {
            e.printStackTrace();
            result.reject(null, "Ocurrió un error inesperado durante el registro: " + e.getMessage());
            return "auth/registro";
        }
    }
}
