package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.AdminDashboardStatsDTO;
import com.isai.gym.app.services.AdminDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashBoardController {

    private final AdminDashboardService adminDashboardService;

    public DashBoardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("")
    public String mostrarDashBoard(Model model) {
        AdminDashboardStatsDTO stats = adminDashboardService.getDashboardStats();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }
}