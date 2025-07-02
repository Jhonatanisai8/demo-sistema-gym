package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.dtos.ProductoTiendaDTO;
import com.isai.gym.app.enums.CategoriaProducto;
import com.isai.gym.app.services.ProductoService;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cliente/productos")
public class ClienteTiendaController {

    private final ProductoService productoService;

    public ClienteTiendaController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/tienda")
    public String mostrarTienda(
            @RequestParam(value = "categoria", required = false) String categoriaParam,
            @RequestParam(value = "search", required = false) String keyword,
            Model model) {

        List<ProductoTiendaDTO> productos;
        try {
            if (categoriaParam != null && !categoriaParam.isEmpty()) {
                CategoriaProducto categoria = CategoriaProducto.valueOf(categoriaParam.toUpperCase());
                productos = productoService.obtenerProductosActivosPorCategoria(categoria);
                model.addAttribute("categoriaSeleccionada", categoriaParam);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                productos = productoService.buscarProductosActivos(keyword);
                model.addAttribute("keyword", keyword);
            } else {
                productos = productoService.obtenerTodosLosProductosActivosTienda();
            }
            model.addAttribute("productos", productos);
            model.addAttribute("categorias", CategoriaProducto.values());
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Categoría no válida: " + categoriaParam);
            productos = productoService.obtenerTodosLosProductosActivosTienda();
            model.addAttribute("productos", productos);
            model.addAttribute("categorias", CategoriaProducto.values());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar los productos: " + e.getMessage());
            productos = productoService.obtenerTodosLosProductosActivosTienda();
            model.addAttribute("productos", productos);
            model.addAttribute("categorias", CategoriaProducto.values());
        }
        return "cliente/productos/tienda";
    }
}
