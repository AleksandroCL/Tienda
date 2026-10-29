package com.proyecto.tienda.controller;

import com.proyecto.tienda.dto.PlataformaFormDTO;
import com.proyecto.tienda.exception.CampoInvalidoException;
import com.proyecto.tienda.service.PlataformaService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/plataformas")
public class AdminPlataformaController {
    private final PlataformaService plataformas;
    public AdminPlataformaController(PlataformaService plataformas) { this.plataformas = plataformas; }

    @GetMapping
    public String listarPlataformas(@RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page, Model model) {
        page = Math.max(0, page);
        var resultado = plataformas.listarPlataformasPaginadas(buscar, PageRequest.of(page, 10));
        model.addAttribute("plataformas", resultado.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("buscarActual", buscar);
        return "admin/plataforma/plataformas";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("plataformaDto", new PlataformaFormDTO());
        return formulario(null, model);
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        var plataforma = plataformas.obtenerPlataformaPorId(id);
        var dto = new PlataformaFormDTO();
        dto.setNombre(plataforma.getNombre());
        model.addAttribute("plataformaDto", dto);
        return formulario(id, model);
    }

    @PostMapping("/nueva")
    public String guardarPlataforma(@Valid @ModelAttribute("plataformaDto") PlataformaFormDTO dto,
            BindingResult result, Model model, RedirectAttributes redirect) {
        return guardar(null, dto, result, model, redirect);
    }

    @PostMapping("/editar/{id}")
    public String actualizarPlataforma(@PathVariable Long id,
            @Valid @ModelAttribute("plataformaDto") PlataformaFormDTO dto,
            BindingResult result, Model model, RedirectAttributes redirect) {
        plataformas.obtenerPlataformaPorId(id);
        return guardar(id, dto, result, model, redirect);
    }

    private String guardar(Long id, PlataformaFormDTO dto, BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                plataformas.guardar(id, dto);
                redirect.addFlashAttribute("exito", "Plataforma guardada correctamente.");
                return "redirect:/admin/plataformas";
            } catch (CampoInvalidoException e) {
                result.rejectValue(e.getCampo(), "duplicado", e.getMessage());
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("nombre", "duplicado", "No se pudo guardar. Comprueba si el nombre ya está en uso.");
            }
        }
        return formulario(id, model);
    }

    private String formulario(Long id, Model model) {
        model.addAttribute("editando", id != null);
        model.addAttribute("plataformaId", id);
        return "admin/plataforma/plataforma-form";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPlataforma(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            plataformas.eliminarPlataforma(id);
            redirect.addFlashAttribute("exito", "Plataforma eliminada correctamente.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error", "No se puede eliminar una plataforma con juegos asociados.");
        }
        return "redirect:/admin/plataformas";
    }
}
