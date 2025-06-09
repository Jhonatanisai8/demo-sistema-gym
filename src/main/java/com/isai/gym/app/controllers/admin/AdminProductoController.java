package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.ProductoDTO;
import com.isai.gym.app.services.impl.ProductoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/productos")
public class AdminProductoController {


    private final ProductoServiceImpl productoService;

    @GetMapping({"/lista", "/"})
    public String listarProductos(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "nombre") String sortBy,
                                  @RequestParam(defaultValue = "asc") String sortDir,
                                  @RequestParam(required = false) String keyword,
                                  Model model) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductoDTO> productosPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            productosPage = productoService.buscarProductos(keyword, pageable);
        } else {
            productosPage = productoService.obtenerTodosLosProductos(pageable);
        }

        model.addAttribute("productosPage", productosPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        int totalPages = productosPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/productos/listar";
    }

}
