package com.isai.gym.app.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/dashboard")
public class DashBoardController {

    @GetMapping(path = "")
    public String mostrarDashBoard() {
        return "dashboard";
    }
}
