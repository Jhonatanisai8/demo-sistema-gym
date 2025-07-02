package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.dtos.CarritoDTO;
import com.isai.gym.app.dtos.VentaRequestDTO;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.enums.MetodoPago;
import com.isai.gym.app.services.CarritoService;
import com.isai.gym.app.services.UsuarioService;
import com.isai.gym.app.services.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente/venta")
public class ClientVentaController {

    private final VentaService ventaService;
    private final CarritoService carritoService;
    private final UsuarioService usuarioService;

    public ClientVentaController(VentaService ventaService, CarritoService carritoService, UsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/checkout")
    public String mostrarCheckout(HttpSession session, Model model) {
        CarritoDTO carrito = carritoService.getCarrito(session);
        if (carrito.getItems().isEmpty()) {
            return "redirect:/cliente/carrito";
        }
        model.addAttribute("carrito", carrito);
        model.addAttribute("ventaRequest", new VentaRequestDTO());
        model.addAttribute("metodosPago", MetodoPago.values());
        return "cliente/venta/checkout";
    }

    @PostMapping("/procesar")
    public String procesarVenta(@ModelAttribute("ventaRequest") VentaRequestDTO ventaRequest,
                                HttpSession session,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        CarritoDTO carrito = carritoService.getCarrito(session);
        if (carrito.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tu carrito está vacío.");
            return "redirect:/cliente/carrito";
        }

        try {
            String username = authentication.getName();
            Usuario usuario = usuarioService.buscarPorNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            ventaService.crearVenta(carrito, usuario, ventaRequest.getMetodoPago());

            carritoService.limpiarCarrito(session);

            redirectAttributes.addFlashAttribute("successMessage", "¡Compra realizada con éxito!");
            return "redirect:/cliente/venta/confirmacion";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar la venta: " + e.getMessage());
            return "redirect:/cliente/venta/checkout";
        }
    }

    @GetMapping("/confirmacion")
    public String mostrarConfirmacion() {
        return "cliente/venta/confirmacion_compra";
    }
}