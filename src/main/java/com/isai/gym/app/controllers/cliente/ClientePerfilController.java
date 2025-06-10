package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.dtos.RegistroUsuarioDTO;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.enums.TipoUsuario;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/editar")
    public String mostrarFormularioEditarUsuario(Model model, RedirectAttributes redirectAttributes,
                                                 Principal principal) {
        Usuario usuario = usuarioService.obtenerPorNombre(principal.getName());
        System.out.println(usuario.getId());
        System.out.println(usuario.getRutaImagen());
        if (usuario != null) {
            RegistroUsuarioDTO registroUsuarioDTO = new RegistroUsuarioDTO();
            registroUsuarioDTO.setId(usuario.getId());
            registroUsuarioDTO.setNombreUsuario(usuario.getNombreUsuario());
            registroUsuarioDTO.setEmail(usuario.getEmail());
            registroUsuarioDTO.setNombreCompleto(usuario.getNombreCompleto());
            registroUsuarioDTO.setFechaNacimiento(usuario.getFechaNacimiento());
            registroUsuarioDTO.setTelefono(usuario.getTelefono());
            registroUsuarioDTO.setGenero(usuario.getGenero());
            registroUsuarioDTO.setPeso(usuario.getPeso());
            registroUsuarioDTO.setAltura(usuario.getAltura());
            registroUsuarioDTO.setDireccion(usuario.getDireccion());
            registroUsuarioDTO.setContactoEmergencia(usuario.getContactoEmergencia());
            registroUsuarioDTO.setTelefonoEmergencia(usuario.getTelefonoEmergencia());
            registroUsuarioDTO.setRol(usuario.getRol());

            model.addAttribute("registroUsuarioDTO", registroUsuarioDTO);
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", TipoUsuario.values());
            // return "admin/usuarios/editar";
            return "cliente/perfil/editar";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado para editar.");
            return "redirect:/cliente/perfil";
        }
    }

    @PostMapping("/editar")
    public String actualizarUsuario(
            @Valid @ModelAttribute("registroUsuarioDTO") RegistroUsuarioDTO usuarioDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model, Principal principal) {
        Usuario usuario = usuarioService.obtenerPorNombre(principal.getName());
        System.out.println(usuario.getRutaImagen());
        Long id = usuario.getId();
        System.out.println(usuario.getId() + "User");
        if (!usuarioDTO.getPassword().isEmpty() || !usuarioDTO.getConfirmPassword().isEmpty()) {
            if (!usuarioDTO.getPassword().equals(usuarioDTO.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "passwords.mismatch", "Las contraseñas no coinciden.");
            } else if (usuarioDTO.getPassword().length() < 6) {
                result.rejectValue("password", "password.length", "La contraseña debe tener al menos 6 caracteres.");
            }
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, corrige los errores del formulario.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registroUsuarioDTO", result);
            redirectAttributes.addFlashAttribute("registroUsuarioDTO", usuarioDTO);
            model.addAttribute("roles", TipoUsuario.values());
            usuarioService.obtenerPorId(id).ifPresent(u -> model.addAttribute("usuario", u));
            return "redirect:/cliente/perfil/editar";
        }
        try {
            usuarioService.actualizarUsuario(id, usuarioDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado exitosamente.");
            return "redirect:/cliente/perfil";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registroUsuarioDTO", result);
            redirectAttributes.addFlashAttribute("registroUsuarioDTO", usuarioDTO);
            model.addAttribute("roles", TipoUsuario.values());
            usuarioService.obtenerPorId(id).ifPresent(u -> model.addAttribute("usuario", u));
            return "redirect:/cliente/perfil/editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registroUsuarioDTO", result);
            redirectAttributes.addFlashAttribute("registroUsuarioDTO", usuarioDTO);
            model.addAttribute("roles", TipoUsuario.values());
            usuarioService.obtenerPorId(id).ifPresent(u -> model.addAttribute("usuario", u));
            return "redirect:/cliente/perfil/editar";
        }
    }


}
