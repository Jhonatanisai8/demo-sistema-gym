package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.MembresiaDTO;
import com.isai.gym.app.entities.Membresia;
import com.isai.gym.app.services.impl.MembresiaServiceImpl;
import jakarta.validation.*;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping(path = "/admin/membresias/tipos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTipoMembresiaController {

    private final MembresiaServiceImpl membresiaService;

    @GetMapping({"", "/"})
    public String listarTiposMembresia(Model model,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "id,asc") String[] sort,
                                       @RequestParam(required = false) String keyword) {

        String sortBy = sort[0];
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Membresia> membresiasPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            membresiasPage = membresiaService.buscar(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            membresiasPage = membresiaService.obtenerMembresias(pageable);
        }

        model.addAttribute("membresias", membresiasPage);
        model.addAttribute("currentPage", membresiasPage.getNumber());
        model.addAttribute("totalPages", membresiasPage.getTotalPages());
        model.addAttribute("totalItems", membresiasPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", direction.toString());
        model.addAttribute("reverseSortDirection", direction.equals(Sort.Direction.ASC) ? "desc" : "asc");

        int totalPages = membresiasPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/membresias/tipos/lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("membresiaDTO", new MembresiaDTO());
        return "admin/membresias/tipos/crear";
    }

    @PostMapping("/crear")
    public String crearMembresia(@Valid @ModelAttribute("membresiaDTO") MembresiaDTO membresiaDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/membresias/tipos/crear";
        }
        try {
            if (membresiaService.existeNombre(membresiaDTO.getNombre(), null)) {
                bindingResult.rejectValue("nombre", "error.membresiaDTO", "Ya existe un tipo de membresía con este nombre.");
                return "admin/membresias/tipos/crear";
            }
            membresiaService.guardarMembresia(membresiaDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de membresía creado exitosamente!");
            return "redirect:/admin/membresias/tipos";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("nombre", "error.membresiaDTO", e.getMessage());
            return "admin/membresias/tipos/crear";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el tipo de membresía: " + e.getMessage());
            return "redirect:/admin/membresias/tipos/crear";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Membresia> membresiaOptional = membresiaService.obtenerMembresiaId(id);
        if (membresiaOptional.isPresent()) {
            Membresia membresia = membresiaOptional.get();
            MembresiaDTO membresiaDTO = new MembresiaDTO();
            membresiaDTO.setId(membresia.getId());
            membresiaDTO.setNombre(membresia.getNombre());
            membresiaDTO.setDescripcion(membresia.getDescripcion());
            membresiaDTO.setPrecio(membresia.getPrecio());
            membresiaDTO.setDuracionDias(membresia.getDuracionDias());
            membresiaDTO.setActiva(membresia.getActiva());
            membresiaDTO.setBeneficios(membresia.getBeneficios());
            membresiaDTO.setLimiteAccesosDia(membresia.getLimiteAccesosDia());

            model.addAttribute("membresiaDTO", membresiaDTO);
            return "admin/membresias/tipos/detalle";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Tipo de membresía no encontrado.");
            return "redirect:/admin/membresias/tipos";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarMembresia(@PathVariable Long id,
                                      @Valid @ModelAttribute("membresiaDTO") MembresiaDTO membresiaDTO,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (!id.equals(membresiaDTO.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error de seguridad: ID de membresía no coincide.");
            return "redirect:/admin/membresias/tipos";
        }
        if (bindingResult.hasErrors()) {
            return "admin/membresias/tipos/detalle";
        }
        try {
            Optional<Membresia> updatedMembresia = membresiaService.actualizar(id, membresiaDTO);

            if (updatedMembresia.isPresent()) {
                redirectAttributes.addFlashAttribute("successMessage", "Tipo de membresía actualizado exitosamente!");
                return "redirect:/admin/membresias/tipos";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Tipo de membresía no encontrado para actualizar.");
                return "redirect:/admin/membresias/tipos";
            }
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("nombre", "error.membresiaDTO", e.getMessage());
            return "admin/membresias/tipos/detalle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el tipo de membresía: " + e.getMessage());
            return "redirect:/admin/membresias/tipos/editar/" + id;
        }
    }

    @GetMapping("/eliminar/{id}")
    public String confirmarEliminarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Membresia> membresiaBD = membresiaService.obtenerMembresiaId(id);
        if (membresiaBD.isPresent()) {
            model.addAttribute("membresiaDTO", membresiaBD.get());
            return "admin/membresias/tipos/confirmar-eliminar";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Membresia no encontrada.");
            return "redirect:/admin/membresias/tipos";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMembresia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (membresiaService.eliminar(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Membresia eliminada exitosamente.");

        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el tipo de membresía. Podría estar asociado a membresías de clientes.");
        }
        return "redirect:/admin/membresias/tipos";
    }

    @PostMapping("/toggleActiva/{id}")
    public String toggleActiva(@PathVariable Long id,
                               @RequestParam("activa") boolean activa, // Recibe el nuevo estado desde el formulario
                               RedirectAttributes redirectAttributes) {
        Optional<Membresia> membresiaOptional = membresiaService.cambiarEstado(id, activa);
        if (membresiaOptional.isPresent()) {
            String mensaje = membresiaOptional.get().getActiva() ? "activado" : "desactivado";
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de membresía '" + membresiaOptional.get().getNombre() + "' " + mensaje + " exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Tipo de membresía no encontrado.");
        }
        return "redirect:/admin/membresias/tipos";
    }
}
