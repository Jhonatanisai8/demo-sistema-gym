package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.EquipoDTO;
import com.isai.gym.app.entities.Equipo;
import com.isai.gym.app.enums.EstadoEquipo;
import com.isai.gym.app.enums.TipoEquipo;
import com.isai.gym.app.services.impl.EquipoServiceImpl;
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
import java.util.stream.IntStream;

@RequestMapping(path = "/admin/equipos")
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminEquipoController {
    private final EquipoServiceImpl equipoService;

    @GetMapping(path = {"", "/lista"})
    public String listarEquipos(@RequestParam(name = "page", defaultValue = "0") int page,
                                // Página actual (0-indexed)
                                @RequestParam(name = "size", defaultValue = "10") int size,
                                // Tamaño de elementos por página
                                @RequestParam(name = "search", required = false) String searchTerm,
                                // Término de búsqueda opcional
                                Model model) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size);
        Page<Equipo> equipoPage;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            equipoPage = equipoService.buscarEquiposPorNombre(searchTerm, pageable);
        } else {
            equipoPage = equipoService.obtenerEquipos(pageable);
        }
        model.addAttribute("equiposPage", equipoPage);

        //generamos la lista con paginacion
        int totalPaginas = equipoPage.getTotalPages();
        if (totalPaginas > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPaginas)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("titulo", "Lista de Equipos del GYM");
        model.addAttribute("searchTerm", searchTerm);
        return "admin/equipos/lista";
    }

    @GetMapping(path = "/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("equipoDTO", new EquipoDTO());
        model.addAttribute("TipoEquipo", TipoEquipo.values());
        model.addAttribute("EstadoEquipo", EstadoEquipo.values());
        return "admin/equipos/crear";
    }

    @PostMapping("/crear")
    public String crearEquipo(@Valid @ModelAttribute("equipoDTO") EquipoDTO equipoDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/equipos/crear";
        }
        try {
            equipoService.guardarEquipo(equipoDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Equipo registrado exitosamente!");
            return "redirect:/admin/equipos";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("numeroSerie", "error.equipoDTO", e.getMessage());
            return "admin/equipos/crear";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar el equipo: " + e.getMessage());
            return "redirect:/admin/equipos/crear";
        }
    }

    @GetMapping(path = "/detalle/{id}")
    public String verDetallesEquipo(@PathVariable Long id,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        Optional<Equipo> equipoBD = equipoService.obtenerPorId(id);
        if (equipoBD.isPresent()) {
            model.addAttribute("equipo", equipoBD.get());
            return "admin/equipos/detalles";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Equipo no encontrado.");
            return "redirect:/admin/equipos";
        }
    }

    @GetMapping(path = "/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Equipo> equipo = equipoService.obtenerPorId(id);
        if (equipo.isPresent()) {
            model.addAttribute("equipoDTO", equipo.get());
            return "admin/equipos/editar";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Equipo no encontrado.");
            return "redirect:/admin/equipos";
        }
    }


}
