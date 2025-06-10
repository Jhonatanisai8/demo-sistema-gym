package com.isai.gym.app.controllers.cliente;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente/dashboard")
@PreAuthorize("hasRole('ROLE_CLIENTE')")
public class ClienteDashBoardController {

    @GetMapping("")
    public String mostrarDashBoard() {
        return "cliente/dashboard";
    }
}