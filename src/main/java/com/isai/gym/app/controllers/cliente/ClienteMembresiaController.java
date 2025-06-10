package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.entities.Membresia;
import com.isai.gym.app.services.impl.MembresiaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cliente/membresias")
@RequiredArgsConstructor
public class ClienteMembresiaController {

    private final MembresiaServiceImpl membresiaService;

    @GetMapping("/comprar")
    public String shopMemberships(Model model) {
        List<Membresia> membresiasDisponibles = membresiaService.obtenerMembresias();
        model.addAttribute("membresiasDisponibles", membresiasDisponibles);

        if (model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", model.asMap().get("successMessage"));
        }
        if (model.containsAttribute("errorMessage")) {
            model.addAttribute("errorMessage", model.asMap().get("errorMessage"));
        }
        return "cliente/membresias/tienda";
    }
}
