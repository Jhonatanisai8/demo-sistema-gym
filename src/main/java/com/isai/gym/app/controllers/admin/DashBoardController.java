package com.isai.gym.app.controllers.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashBoardController {

    @GetMapping("")
    public String mostrarDashBoard() {
        return "admin/dashboard";
    }
}