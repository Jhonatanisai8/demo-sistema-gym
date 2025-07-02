package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.services.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente/carrito")
public class ClientCarritoController {

    private final CarritoService carritoService;

    public ClientCarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("")
    public String verCarrito(HttpSession session, Model model) {
        model.addAttribute("carrito", carritoService.getCarrito(session));
        return "cliente/carrito/ver_carrito"; // Vista para mostrar el carrito
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam("idProducto") Long idProducto,
                                   @RequestParam(value = "cantidad", defaultValue = "1") int cantidad,
                                   HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            // CORRECCIÓN: El método se llama agregarProducto
            carritoService.agregarProducto(session, idProducto, cantidad);
            redirectAttributes.addFlashAttribute("successMessage", "Producto añadido al carrito con éxito.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cliente/productos/tienda"; // Redirigir a la tienda de productos
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam("idProducto") Long idProducto,
                                     @RequestParam("cantidad") int cantidad,
                                     HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            carritoService.actualizarCantidad(session, idProducto, cantidad);
            redirectAttributes.addFlashAttribute("successMessage", "Cantidad actualizada.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cliente/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminarDelCarrito(@RequestParam("idProducto") Long idProducto, HttpSession session, RedirectAttributes redirectAttributes) {
        // CORRECCIÓN: El método se llama eliminarProducto
        carritoService.eliminarProducto(session, idProducto);
        redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado del carrito.");
        return "redirect:/cliente/carrito";
    }
}