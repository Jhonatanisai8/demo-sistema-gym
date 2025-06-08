package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.entities.Equipo;
import com.isai.gym.app.services.impl.EquipoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.IntStream;

@RequestMapping(path = "/admin/equipos")
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class EquipoAdminController {
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
}
