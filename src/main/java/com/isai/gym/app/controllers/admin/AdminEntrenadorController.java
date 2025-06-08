package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.EntrenadorDTO;
import com.isai.gym.app.entities.Entrenador;
import com.isai.gym.app.services.impl.EntrenadorServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/entrenadores")
public class AdminEntrenadorController {
    private final EntrenadorServiceImpl entrenadorService;

    @GetMapping({"", "/"})
    public String listarEntrenadores(Model model,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "nombre,asc") String[] sort,
                                     @RequestParam(required = false) String keyword) {

        String sortBy = sort[0];
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Entrenador> entrenadorPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            entrenadorPage = entrenadorService.buscar(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            entrenadorPage = entrenadorService.obtenerEntrenadores(pageable);
        }

        model.addAttribute("entrenadores", entrenadorPage);
        model.addAttribute("currentPage", entrenadorPage.getNumber());
        model.addAttribute("totalPages", entrenadorPage.getTotalPages());
        model.addAttribute("totalItems", entrenadorPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", direction.toString());
        model.addAttribute("reverseSortDirection", direction.equals(Sort.Direction.ASC) ? "desc" : "asc");

        int totalPages = entrenadorPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/entrenadores/lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("entrenadorDTO", new EntrenadorDTO());
        return "admin/entrenadores/crear";
    }

    @PostMapping("/crear")
    public String crearEntrenador(@Valid @ModelAttribute("entrenadorDTO") EntrenadorDTO entrenadorDTO,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/entrenadores/crear";
        }

        try {
            entrenadorService.guardar(entrenadorDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Entrenador creado exitosamente!");
            return "redirect:/admin/entrenadores";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("nombre", "error.entrenadorDTO", e.getMessage()); // O el campo específico que causó el error
            return "admin/entrenadores/crear";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el entrenador: " + e.getMessage());
            return "redirect:/admin/entrenadores/crear";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarDetalleOFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Entrenador> entrenadorOptional = entrenadorService.findById(id);
        if (entrenadorOptional.isPresent()) {
            Entrenador entrenador = entrenadorOptional.get();
            //  entidad a un DTO para el formulario
            EntrenadorDTO entrenadorDTO = new EntrenadorDTO();
            entrenadorDTO.setId(entrenador.getId());
            entrenadorDTO.setNombre(entrenador.getNombre());
            entrenadorDTO.setEspecialidad(entrenador.getEspecialidad());
            entrenadorDTO.setTelefono(entrenador.getTelefono());
            entrenadorDTO.setEmail(entrenador.getEmail());
            entrenadorDTO.setFechaContratacion(entrenador.getFechaContratacion());
            entrenadorDTO.setActivo(entrenador.getActivo());
            entrenadorDTO.setTarifaPorSesion(entrenador.getTarifaPorSesion());
            entrenadorDTO.setCertificaciones(entrenador.getCertificaciones());
            entrenadorDTO.setHorarioDisponible(entrenador.getHorarioDisponible());
            entrenadorDTO.setRutaImagenActual(entrenador.getRutaImagen());

            model.addAttribute("entrenadorDTO", entrenadorDTO);
            model.addAttribute("entrenador", entrenador);
            return "admin/entrenadores/detalle";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Entrenador no encontrado.");
            return "redirect:/admin/entrenadores";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarEntrenador(@PathVariable Long id,
                                       @Valid @ModelAttribute("entrenadorDTO") EntrenadorDTO entrenadorDTO,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (!id.equals(entrenadorDTO.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error de seguridad: ID de entrenador no coincide.");
            return "redirect:/admin/entrenadores";
        }

        if (bindingResult.hasErrors()) {
            // si hay errores, repoblar el modelo con la entidad actual para la imagen.
            Optional<Entrenador> originalEntrenador = entrenadorService.findById(id);
            originalEntrenador.ifPresent(entrenador -> model.addAttribute("entrenador", entrenador));
            return "admin/entrenadores/detalle";
        }

        try {
            Optional<Entrenador> updatedEntrenador = entrenadorService.actualizar(id, entrenadorDTO);

            if (updatedEntrenador.isPresent()) {
                redirectAttributes.addFlashAttribute("successMessage", "Entrenador actualizado exitosamente!");
                return "redirect:/admin/entrenadores";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Entrenador no encontrado para actualizar.");
                return "redirect:/admin/entrenadores";
            }
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("nombre", "error.entrenadorDTO", e.getMessage());
            Optional<Entrenador> originalEntrenador = entrenadorService.findById(id); // recargar para mostrar la imagen actual
            originalEntrenador.ifPresent(entrenador -> model.addAttribute("entrenador", entrenador));
            return "admin/entrenadores/detalle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el entrenador: " + e.getMessage());
            return "redirect:/admin/entrenadores/editar/" + id;
        }
    }

    @PostMapping("/toggleActivo/{id}")
    public String toggleActivo(@PathVariable Long id,
                               @RequestParam("activo") boolean activo,
                               RedirectAttributes redirectAttributes) {
        Optional<Entrenador> entrenadorOptional = entrenadorService.toggleActivo(id, activo);
        if (entrenadorOptional.isPresent()) {
            String mensaje = entrenadorOptional.get().getActivo() ? "activado" : "desactivado";
            redirectAttributes.addFlashAttribute("successMessage", "Entrenador '" + entrenadorOptional.get().getNombre() + "' " + mensaje + " exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Entrenador no encontrado.");
        }
        return "redirect:/admin/entrenadores";
    }

    @GetMapping(path = "/eliminar/{id}")
    public String confirmarEliminarEntrenador(@PathVariable Long id,
                                              Model model,
                                              RedirectAttributes redirectAttributes) {
        Optional<Entrenador> entrenadorOptional = entrenadorService.findById(id);
        if (entrenadorOptional.isPresent()) {
            model.addAttribute("entrenador", entrenadorOptional.get());
            return "admin/entrenadores/confirmar-eliminar";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Entrenador no encontrado.");
            return "redirect:/admin/entrenadores";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarEntrenador(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            entrenadorService.eliminarPorID(id);
            redirectAttributes.addFlashAttribute("successMessage", "Entrenador eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el entrenador: " + e.getMessage());
        }
        return "redirect:/admin/entrenadores";
    }

}
