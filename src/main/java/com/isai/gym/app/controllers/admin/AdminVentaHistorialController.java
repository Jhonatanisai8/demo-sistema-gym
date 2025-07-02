package com.isai.gym.app.controllers.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.isai.gym.app.dtos.VentaDetalleAdminDTO;
import com.isai.gym.app.dtos.VentaHistorialDTO;
import com.isai.gym.app.dtos.VentaHistorialFilterDTO;
import com.isai.gym.app.enums.EstadoVenta;
import com.isai.gym.app.services.VentaService;

import lombok.*;

@Controller
@RequestMapping("/admin/venta-historial")
@RequiredArgsConstructor
public class AdminVentaHistorialController {

    private final VentaService ventaHistorialAdminService;

    @GetMapping("/historial")
    public String mostrarHistorialVentas(
            @ModelAttribute("filtros") VentaHistorialFilterDTO filtros,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<VentaHistorialDTO> ventasPage = ventaHistorialAdminService.obtenerHistorialVentas(filtros, pageable);

        model.addAttribute("ventasPage", ventasPage);
        model.addAttribute("filtros", filtros);
        model.addAttribute("estadosVenta", EstadoVenta.values());

        int totalPages = ventasPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "admin/ventas/historial";
    }

    @GetMapping("/{id}")
    public String mostrarDetalleVenta(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<VentaDetalleAdminDTO> ventaDetalleOpt = ventaHistorialAdminService.obtenerDetalleVenta(id);
        if (ventaDetalleOpt.isPresent()) {
            model.addAttribute("venta", ventaDetalleOpt.get());
            return "admin/ventas/detalle";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Venta no encontrada con ID: " + id);
            return "redirect:/admin/venta-historial";
        }
    }
}
