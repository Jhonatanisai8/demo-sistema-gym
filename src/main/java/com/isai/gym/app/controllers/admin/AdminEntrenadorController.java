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


}
