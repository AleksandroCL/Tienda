package com.proyecto.tienda.controller;

import com.proyecto.tienda.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CarritoService carritoService;

    // Este método se ejecutará automáticamente antes de cargar CUALQUIER vista HTML
    @ModelAttribute("cantidadCarrito")
    public int globalCantidadCarrito(HttpSession session) {
        // Le enviamos al Navbar la cantidad total de unidades en el carrito
        return carritoService.obtenerCantidadTotal(session);
    }
}