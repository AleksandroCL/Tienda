package com.proyecto.tienda.controller;

import com.proyecto.tienda.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final DashboardService dashboard;

    public AdminController(DashboardService dashboard) { this.dashboard = dashboard; }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        model.addAttribute("resumen", dashboard.obtenerResumen());
        return "admin/dashboard";
    }
}
