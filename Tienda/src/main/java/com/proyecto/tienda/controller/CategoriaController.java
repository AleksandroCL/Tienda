package com.proyecto.tienda.controller;

import com.proyecto.tienda.dto.CategoriaFormDTO;
import com.proyecto.tienda.exception.CampoInvalidoException;
import com.proyecto.tienda.repository.CategoriaRepository;
import com.proyecto.tienda.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaController {
    private final CategoriaRepository categorias;
    private final CategoriaService servicio;
    public CategoriaController(CategoriaRepository categorias, CategoriaService servicio) {
        this.categorias = categorias;
        this.servicio = servicio;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categorias.findAll());
        return "admin/categorias/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("categoriaDto", new CategoriaFormDTO());
        return formulario(null, model);
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        var dto = new CategoriaFormDTO();
        dto.setNombre(servicio.obtenerPorId(id).getNombre());
        model.addAttribute("categoriaDto", dto);
        return formulario(id, model);
    }

    @PostMapping("/nueva")
    public String guardar(@Valid @ModelAttribute("categoriaDto") CategoriaFormDTO dto, BindingResult result, Model model) {
        return guardarFormulario(null, dto, result, model);
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("categoriaDto") CategoriaFormDTO dto,
            BindingResult result, Model model) {
        servicio.obtenerPorId(id);
        return guardarFormulario(id, dto, result, model);
    }

    private String guardarFormulario(Long id, CategoriaFormDTO dto, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                servicio.guardar(id, dto);
                return "redirect:/admin/categorias";
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
        model.addAttribute("categoriaId", id);
        return "admin/categorias/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            servicio.eliminar(id);
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorEliminar", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("errorEliminar", "No se puede eliminar una categoría con juegos asociados.");
        }
        return "redirect:/admin/categorias";
    }
}
