package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.entities.Membresia;
import com.isai.gym.app.services.impl.MembresiaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping(path = "/admin/membresias/tipos")
@RequiredArgsConstructor
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

}
