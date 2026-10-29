package com.proyecto.tienda.controller;

import com.proyecto.tienda.service.SoporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/soporte")
public class AdminSoporteController {
    private final SoporteService soporte;
    public AdminSoporteController(SoporteService soporte) { this.soporte = soporte; }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page, Model model) {
        var resultado = soporte.listar(page);
        model.addAttribute("consultas", resultado.getContent());
        model.addAttribute("paginaActual", resultado.getNumber());
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("totalConsultas", resultado.getTotalElements());
        return "admin/soporte/consultas";
    }
}
