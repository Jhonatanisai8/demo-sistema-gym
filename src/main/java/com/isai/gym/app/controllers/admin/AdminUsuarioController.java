package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.RegistroUsuarioDTO;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.enums.TipoUsuario;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequestMapping(path = "/admin/usuarios")
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUsuarioController {

    private final UsuarioServiceImpl usuarioService;

    @GetMapping(path = "/lista")
    public String listarUsuarios(@RequestParam(name = "page", defaultValue = "0") int page, // Página actual (0-indexed)
                                 @RequestParam(name = "size", defaultValue = "10") int size, // Tamaño de elementos por página
                                 @RequestParam(name = "search", required = false) String searchTerm, // Término de búsqueda opcional
                                 Model model) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size);
        Page<Usuario> usuarioPage;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            usuarioPage = usuarioService.obtenerPorNombreOEmail(searchTerm, pageable);
        } else {
            usuarioPage = usuarioService.obtenerUsuarios(pageable);
        }
        model.addAttribute("usuariosPage", usuarioPage);
        //generamos la lista con paginacion
        int totalPaginas = usuarioPage.getTotalPages();
        if (totalPaginas > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPaginas)
                    .boxed().
                    collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("tittulo", "Listado de Usuarios.");
        model.addAttribute("searchTerm", searchTerm);
        return "admin/usuarios/lista";
    }

    //metodo para ver los detalles
    @GetMapping("/detalle/{id}")
    public String verUsuario(@PathVariable Long id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOptional = usuarioService.obtenerPorId(id);
        if (usuarioOptional.isPresent()) {
            model.addAttribute("usuario", usuarioOptional.get());
            return "admin/usuarios/detalles";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
            return "redirect:/admin/usuarios";
        }
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoUsuario(Model model) {
        model.addAttribute("registroUsuarioDTO", new RegistroUsuarioDTO());
        model.addAttribute("roles", TipoUsuario.values());
        return "admin/usuarios/nuevo";
    }

    @PostMapping("/nuevo")
    public String guardarNuevoUsuario(
            @Valid @ModelAttribute("registroUsuarioDTO") RegistroUsuarioDTO registroUsuarioDTO,
            BindingResult result,
            @RequestParam("rolSeleccionado") String rolString,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (!registroUsuarioDTO.getPassword().equals(registroUsuarioDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "passwords.mismatch", "Las contraseñas no coinciden.");
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, corrige los errores del formulario.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registroUsuarioDTO", result);
            redirectAttributes.addFlashAttribute("registroUsuarioDTO", registroUsuarioDTO);
            model.addAttribute("roles", TipoUsuario.values()); // Vuelve a enviar los roles
            return "redirect:/admin/usuarios/nuevo";
        }

        try {
            TipoUsuario rol = TipoUsuario.valueOf(rolString.toUpperCase());
            registroUsuarioDTO.setRol(rol);
            usuarioService.registrarUsuario(registroUsuarioDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario creado exitosamente.");
            return "redirect:/admin/usuarios/lista";
        } catch (IllegalArgumentException e) {
            // capturamos las excepciones de unicidad lanzadas por el servicio
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registroUsuarioDTO", result);
            redirectAttributes.addFlashAttribute("registroUsuarioDTO", registroUsuarioDTO);
            model.addAttribute("roles", TipoUsuario.values());
            return "redirect:/admin/usuarios/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registroUsuarioDTO", result);
            redirectAttributes.addFlashAttribute("registroUsuarioDTO", registroUsuarioDTO);
            model.addAttribute("roles", TipoUsuario.values());
            return "redirect:/admin/usuarios/nuevo";
        }
    }

}
