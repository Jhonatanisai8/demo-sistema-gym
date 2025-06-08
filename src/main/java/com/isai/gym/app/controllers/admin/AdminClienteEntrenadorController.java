package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.ClienteEntrenadorDTO;
import com.isai.gym.app.entities.ClienteEntrenador;
import com.isai.gym.app.services.impl.ClienteEntrenadorServiceImpl;
import com.isai.gym.app.services.impl.EntrenadorServiceImpl;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
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

}
