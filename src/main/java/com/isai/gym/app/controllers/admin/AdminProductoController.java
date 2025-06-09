package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.ProductoDTO;
import com.isai.gym.app.enums.CategoriaProducto;
import com.isai.gym.app.services.impl.ProductoServiceImpl;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
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
                                  @RequestParam(defaultValue = "5") int size,
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

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProducto(Model model) {
        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute("categorias", Arrays.asList(CategoriaProducto.values()));
        return "admin/productos/crear";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") ProductoDTO productoDTO,
                                  BindingResult result,
                                  @RequestParam("fileImagen") MultipartFile fileImagen,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", Arrays.asList(CategoriaProducto.values()));
            model.addAttribute("pageTitle", "Crear Nuevo Producto");
            model.addAttribute("pageHeader", "<i class='bi bi-plus-circle me-2'></i>Crear Nuevo Producto");
            return "admin/productos/crear";
        }

        try {
            if (productoDTO.getActivo() == null) {
                productoDTO.setActivo(true);
            }
            productoService.crearProducto(productoDTO, fileImagen);
            redirectAttributes.addFlashAttribute("successMessage", "Producto guardado exitosamente!");
            return "redirect:/admin/productos/lista";
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("nombre")) {
                result.rejectValue("nombre", "error.producto", e.getMessage());
            } else if (e.getMessage().contains("código de barras")) {
                result.rejectValue("codigoBarras", "error.producto", e.getMessage());
            } else {
                result.reject("globalError", e.getMessage());
            }
            model.addAttribute("categorias", Arrays.asList(CategoriaProducto.values()));
            model.addAttribute("pageTitle", "Crear Nuevo Producto");
            model.addAttribute("pageHeader", "<i class='bi bi-plus-circle me-2'></i>Crear Nuevo Producto");
            return "admin/productos/crear";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el producto: " + e.getMessage());
            model.addAttribute("categorias", Arrays.asList(CategoriaProducto.values()));
            model.addAttribute("pageTitle", "Crear Nuevo Producto");
            model.addAttribute("pageHeader", "<i class='bi bi-plus-circle me-2'></i>Crear Nuevo Producto");
            return "admin/productos/crear";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ProductoDTO productoDTO = productoService.obtenerProductoPorId(id);
            model.addAttribute("producto", productoDTO);
            model.addAttribute("categorias", Arrays.asList(CategoriaProducto.values()));
            return "admin/productos/editar";
        } catch (jakarta.persistence.EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/admin/productos";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarProducto(@PathVariable Long id,
                                     @Valid @ModelAttribute("producto") ProductoDTO productoDTO,
                                     BindingResult result,
                                     @RequestParam("fileImagen") MultipartFile fileImagen,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", Arrays.asList(CategoriaProducto.values()));
            return "admin/productos/editar";
        }

        try {
            productoService.actualizarProducto(id, productoDTO, fileImagen);
            redirectAttributes.addFlashAttribute("successMessage", "Producto actualizado exitosamente!");
            return "redirect:/admin/productos/lista";
        } catch (IllegalArgumentException e) {
            result.rejectValue("nombre", "error.producto", e.getMessage());
            result.rejectValue("codigoBarras", "error.producto", e.getMessage());
            model.addAttribute("categorias", Arrays.asList(CategoriaProducto.values()));
            return "admin/productos/editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el producto: " + e.getMessage());
            model.addAttribute("categorias", Arrays.asList(CategoriaProducto.values()));
            return "admin/productos/editar";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        ProductoDTO productoDTO = productoService.obtenerProductoPorId(id);
        if (productoDTO.getActivo() != null) {
            model.addAttribute("producto", productoDTO);
            return "admin/productos/confirmar-eliminar";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Producto no encontrado.");
            return "redirect:/admin/productos/lista";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el producto: " + e.getMessage());
        }
        return "redirect:/admin/productos/lista";
    }
}
