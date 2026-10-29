package com.proyecto.tienda.controller;

import com.proyecto.tienda.exception.RecursoNoEncontradoException;

import com.proyecto.tienda.dto.JuegoFormDTO;
import com.proyecto.tienda.model.Categoria;
import com.proyecto.tienda.model.Juego;
import com.proyecto.tienda.model.Plataforma;
import com.proyecto.tienda.repository.CategoriaRepository;
import com.proyecto.tienda.repository.JuegoRepository;
import com.proyecto.tienda.repository.PlataformaRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@Controller
@RequestMapping("/juegos")
public class JuegoController {

    @Autowired
    private JuegoRepository juegoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PlataformaRepository plataformaRepository;

    private static final int TAMANO_PAGINA = 12;

    // 1. Mostrar el catálogo de juegos (con búsqueda, filtros y paginación)
    @GetMapping
    public String listarJuegos(@RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long plataformaId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        page = Math.max(0, page);
        Pageable pageable = PageRequest.of(page, TAMANO_PAGINA, Sort.by("titulo").ascending());
        Page<Juego> resultado = juegoRepository.filtrarJuegos(buscar, categoriaId, plataformaId, pageable);

        model.addAttribute("juegos", resultado.getContent());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("plataformas", plataformaRepository.findAll());
        model.addAttribute("buscarActual", buscar);
        model.addAttribute("categoriaSeleccionada", categoriaId);
        model.addAttribute("plataformaSeleccionada", plataformaId);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("totalResultados", resultado.getTotalElements());
        return "juegos/lista";
    }

    // 2. Ver detalles de un juego específico
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El juego solicitado no existe."));
        model.addAttribute("juego", juego);
        return "juegos/detalle";
    }

    // 3. Mostrar el formulario para registrar un nuevo juego
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("juegoDto", new JuegoFormDTO());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("plataformas", plataformaRepository.findAll());
        model.addAttribute("editando", false);
        return "juegos/formulario";
    }

    // 4. Procesar el formulario, validar y guardar (creación)
    @PostMapping("/nuevo")
    public String guardarJuego(@Valid @ModelAttribute("juegoDto") JuegoFormDTO dto,
            BindingResult result,
            Model model) {

        validarReferencias(dto, result);
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("plataformas", plataformaRepository.findAll());
            model.addAttribute("editando", false);
            return "juegos/formulario";
        }

        Juego juego = new Juego();
        aplicarDatosDelFormulario(juego, dto);
        juegoRepository.save(juego);

        return "redirect:/juegos";
    }

    // 5. Mostrar el formulario precargado para editar un juego existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El juego solicitado no existe."));

        JuegoFormDTO dto = new JuegoFormDTO();
        dto.setTitulo(juego.getTitulo());
        dto.setDescripcion(juego.getDescripcion());
        dto.setPrecio(juego.getPrecio());
        dto.setStock(juego.getStock());
        dto.setImagenUrl(juego.getImagenUrl());
        dto.setFechaLanzamiento(juego.getFechaLanzamiento());

        dto.setCategoriaIds(juego.getCategorias().stream()
                .map(Categoria::getId)
                .toList());
        juego.getPlataformas().stream().findFirst()
                .ifPresent(p -> dto.setPlataformaId(p.getId()));

        model.addAttribute("juegoDto", dto);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("plataformas", plataformaRepository.findAll());
        model.addAttribute("editando", true);
        model.addAttribute("juegoId", id);
        return "juegos/formulario";
    }

    // 6. Procesar la edición
    @PostMapping("/editar/{id}")
    public String actualizarJuego(@PathVariable Long id,
            @Valid @ModelAttribute("juegoDto") JuegoFormDTO dto,
            BindingResult result,
            Model model) {

        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El juego solicitado no existe."));

        validarReferencias(dto, result);
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("plataformas", plataformaRepository.findAll());
            model.addAttribute("editando", true);
            model.addAttribute("juegoId", id);
            return "juegos/formulario";
        }

        juego.getCategorias().clear();
        juego.getPlataformas().clear();
        aplicarDatosDelFormulario(juego, dto);
        juegoRepository.save(juego);

        return "redirect:/juegos";
    }

    // 7. Eliminar un juego
    @PostMapping("/eliminar/{id}")
    public String eliminarJuego(@PathVariable Long id) {
        Juego juego = juegoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El juego solicitado no existe."));
        juegoRepository.delete(juego);
        return "redirect:/juegos";
    }

    private void validarReferencias(JuegoFormDTO dto, BindingResult result) {
        if (result.hasErrors()) return;
        if (!plataformaRepository.existsById(dto.getPlataformaId())) {
            result.rejectValue("plataformaId", "inexistente", "La plataforma seleccionada ya no está disponible.");
        }
        var ids = new HashSet<>(dto.getCategoriaIds());
        if (categoriaRepository.findAllById(ids).size() != ids.size()) {
            result.rejectValue("categoriaIds", "inexistente", "Alguna categoría ya no está disponible. Revisa la selección.");
        }
    }

    // Mapea los datos comunes del DTO hacia la entidad (usado en crear y editar)
    private void aplicarDatosDelFormulario(Juego juego, JuegoFormDTO dto) {
        juego.setTitulo(dto.getTitulo());
        juego.setDescripcion(dto.getDescripcion());
        juego.setPrecio(dto.getPrecio());
        juego.setStock(dto.getStock());
        juego.setImagenUrl(dto.getImagenUrl());
        juego.setFechaLanzamiento(dto.getFechaLanzamiento());

        if (dto.getCategoriaIds() != null) {
            dto.getCategoriaIds().forEach(id -> {
                Categoria categoria = categoriaRepository.findById(id).orElse(null);
                if (categoria != null) {
                    juego.getCategorias().add(categoria);
                }
            });
        }

        if (dto.getPlataformaId() != null) {
            Plataforma plataforma = plataformaRepository.findById(dto.getPlataformaId()).orElse(null);
            if (plataforma != null) {
                juego.getPlataformas().add(plataforma);
            }
        }
    }
}
