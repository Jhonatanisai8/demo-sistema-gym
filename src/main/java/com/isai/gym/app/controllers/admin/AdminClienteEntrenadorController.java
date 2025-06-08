package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.ClienteEntrenadorDTO;
import com.isai.gym.app.entities.ClienteEntrenador;
import com.isai.gym.app.services.impl.ClienteEntrenadorServiceImpl;
import com.isai.gym.app.services.impl.EntrenadorServiceImpl;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/admin/entrenadores/asignaciones")
@RequiredArgsConstructor
public class AdminClienteEntrenadorController {
    private final ClienteEntrenadorServiceImpl clienteEntrenadorService;

    private final UsuarioServiceImpl usuarioService;

    private final EntrenadorServiceImpl entrenadorService;

    // Método para cargar datos comunes en los formularios (usuarios y entrenadores)
    private void loadFormDependencies(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios()); // Obtener todos los usuarios
        model.addAttribute("entrenadores", entrenadorService.obtenerEntrenadores()); // Obtener todos los entrenadores
    }

    @GetMapping({"", "/"})
    public String listarAsignaciones(Model model,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "id,asc") String[] sort,
                                     @RequestParam(required = false) String keyword) {

        String sortBy = sort[0];
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ClienteEntrenador> asignacionesPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            asignacionesPage = clienteEntrenadorService.buscar(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            asignacionesPage = clienteEntrenadorService.obtenerClientesEntrenadores(pageable);
        }

        model.addAttribute("asignaciones", asignacionesPage);
        model.addAttribute("currentPage", asignacionesPage.getNumber());
        model.addAttribute("totalPages", asignacionesPage.getTotalPages());
        model.addAttribute("totalItems", asignacionesPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", direction.toString());
        model.addAttribute("reverseSortDirection", direction.equals(Sort.Direction.ASC) ? "desc" : "asc");

        int totalPages = asignacionesPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/entrenadores/asignaciones/lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("clienteEntrenadorDTO", new ClienteEntrenadorDTO());
        loadFormDependencies(model); // cargamos los usuarios y entrenadores
        return "admin/entrenadores/asignaciones/crear";
    }

    @PostMapping("/crear")
    public String crearAsignacion(@Valid @ModelAttribute("clienteEntrenadorDTO") ClienteEntrenadorDTO clienteEntrenadorDTO,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            loadFormDependencies(model);
            return "admin/entrenadores/asignaciones/crear";
        }

        try {
            clienteEntrenadorService.guardar(clienteEntrenadorDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Asignación creada exitosamente!");
            return "redirect:/admin/entrenadores/asignaciones";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage()); // mostramos  el mensaje de conflicto de fechas
            loadFormDependencies(model);
            return "admin/entrenadores/asignaciones/crear";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la asignación: " + e.getMessage());
            return "redirect:/admin/entrenadores/asignaciones/crear";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ClienteEntrenador> asignacionOptional = clienteEntrenadorService.obtenerPorID(id);
        if (asignacionOptional.isPresent()) {
            ClienteEntrenador asignacion = asignacionOptional.get();
            // Mapea la entidad a un DTO para el formulario
            ClienteEntrenadorDTO clienteEntrenadorDTO = new ClienteEntrenadorDTO();
            clienteEntrenadorDTO.setId(asignacion.getId());
            clienteEntrenadorDTO.setUsuarioId(asignacion.getUsuario().getId());
            clienteEntrenadorDTO.setEntrenadorId(asignacion.getEntrenador().getId());
            clienteEntrenadorDTO.setFechaInicio(asignacion.getFechaInicio());
            clienteEntrenadorDTO.setFechaFin(asignacion.getFechaFin());
            System.out.println(asignacion.getFechaFin());

            model.addAttribute("clienteEntrenadorDTO", clienteEntrenadorDTO);
            loadFormDependencies(model); // Carga usuarios y entrenadores
            return "admin/entrenadores/asignaciones/detalle";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Asignación no encontrada.");
            return "redirect:/admin/entrenadores/asignaciones";
        }
    }
}
