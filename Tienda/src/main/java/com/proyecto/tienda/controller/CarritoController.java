package com.proyecto.tienda.controller;

import com.proyecto.tienda.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    /**
     * Mostrar carrito.
     */
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        model.addAttribute("items", carritoService.obtenerItems(session));
        model.addAttribute("total", carritoService.calcularTotal(session));
        model.addAttribute("cantidadTotal", carritoService.obtenerCantidadTotal(session));

        // AGREGAR ESTA LÍNEA (la vista carrito.html la necesita):
        model.addAttribute("cantidadProductos", carritoService.obtenerItems(session).size());

        // Asegúrate de que la ruta apunte correctamente a donde guardaste carrito.html
        return "carrito/carrito";
    }

    /**
     * Agregar juego al carrito.
     */
    @PostMapping("/agregar/{id}")
    public String agregar(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            carritoService.agregar(id, session);
        } catch (IllegalStateException e) {
            // Capturamos la validación de stock y enviamos el mensaje a la vista
            redirectAttributes.addFlashAttribute("errorStock", e.getMessage());
        }

        return "redirect:/carrito";
    }

    /**
     * Aumentar cantidad.
     */
    @PostMapping("/aumentar/{id}")
    public String aumentar(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            carritoService.aumentar(id, session);
        } catch (IllegalStateException e) {
            // Capturamos el límite de stock máximo
            redirectAttributes.addFlashAttribute("errorStock", e.getMessage());
        }

        return "redirect:/carrito";
    }

    /**
     * Disminuir cantidad.
     */
    @PostMapping("/disminuir/{id}")
    public String disminuir(
            @PathVariable Long id,
            HttpSession session) {

        carritoService.disminuir(id, session);
        return "redirect:/carrito";
    }

    /**
     * Eliminar producto.
     */
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            HttpSession session) {

        carritoService.eliminar(id, session);
        return "redirect:/carrito";
    }

    /**
     * Vaciar carrito.
     */
    @PostMapping("/vaciar")
    public String vaciar(HttpSession session) {

        carritoService.vaciar(session);
        return "redirect:/carrito";
    }
}