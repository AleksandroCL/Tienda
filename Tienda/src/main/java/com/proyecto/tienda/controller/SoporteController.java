package com.proyecto.tienda.controller;

import com.proyecto.tienda.dto.ConsultaSoporteDTO;
import com.proyecto.tienda.service.SoporteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/soporte")
public class SoporteController {
    private final SoporteService soporte;

    public SoporteController(SoporteService soporte) { this.soporte = soporte; }

    @GetMapping("/terminos")
    public String terminos(Model model) {
        model.addAttribute("tipoDocumento", "terminos");
        model.addAttribute("tituloDocumento", "Términos y condiciones");
        return "soporte/documento";
    }

    @GetMapping("/privacidad")
    public String privacidad(Model model) {
        model.addAttribute("tipoDocumento", "privacidad");
        model.addAttribute("tituloDocumento", "Política de privacidad");
        return "soporte/documento";
    }

    @GetMapping("/ayuda")
    public String ayuda(@RequestParam(defaultValue = "") String asunto, Model model) {
        var dto = new ConsultaSoporteDTO();
        dto.setAsunto(asunto);
        model.addAttribute("consultaDto", dto);
        return "soporte/ayuda";
    }

    @PostMapping("/consulta")
    public String registrar(@Valid @ModelAttribute("consultaDto") ConsultaSoporteDTO dto,
            BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) return "soporte/ayuda";
        Long id = soporte.registrar(dto);
        redirect.addFlashAttribute("consultaRegistrada", id);
        return "redirect:/soporte/ayuda#contacto";
    }
}
