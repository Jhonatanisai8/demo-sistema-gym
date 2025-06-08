package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.MembresiaClienteDTO;
import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.enums.EstadoMembresia;
import com.isai.gym.app.services.impl.MembresiaClienteServiceImpl;
import com.isai.gym.app.services.impl.MembresiaServiceImpl;
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

@Controller
@RequestMapping("/admin/membresias/clientes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMembresiaClienteController {

    private final MembresiaClienteServiceImpl membresiaClienteServiceImpl;

    private final MembresiaServiceImpl membresiaServiceImpl;

    private final UsuarioServiceImpl usuarioServiceImpl;

    //cargamos datos comunes
    private void loadFormDependencias(Model model) {
        model.addAttribute("usuarios", usuarioServiceImpl.obtenerUsuarios(PageRequest.of(0, 1000, Sort.by("nombreCompleto"))).getContent()); // Cargar todos los usuarios (ajustar paginación si hay muchos)
        model.addAttribute("tiposMembresia", membresiaServiceImpl.obtenerMembresias(PageRequest.of(0, 1000, Sort.by("nombre"))).getContent()); // Cargar todos los tipos de membresía
        model.addAttribute("estadosMembresia", EstadoMembresia.values()); // Enum para el combobox de estados
    }

    @GetMapping({"", "/"})
    public String listarMembresiasCliente(Model model,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "id,asc") String[] sort,
                                          @RequestParam(required = false) String keyword) {

        String sortBy = sort[0];
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<MembresiaCliente> membresiasClientePage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            membresiasClientePage = membresiaClienteServiceImpl.buscar(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            membresiasClientePage = membresiaClienteServiceImpl.obtenerTodasMembresias(pageable);
        }

        model.addAttribute("membresiasCliente", membresiasClientePage);
        model.addAttribute("currentPage", membresiasClientePage.getNumber());
        model.addAttribute("totalPages", membresiasClientePage.getTotalPages());
        model.addAttribute("totalItems", membresiasClientePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", direction.toString());
        model.addAttribute("reverseSortDirection", direction.equals(Sort.Direction.ASC) ? "desc" : "asc");

        int totalPages = membresiasClientePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "admin/membresias/clientes/lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("membresiaClienteDTO", new MembresiaClienteDTO());
        loadFormDependencias(model);
        return "admin/membresias/clientes/crear";
    }

}
