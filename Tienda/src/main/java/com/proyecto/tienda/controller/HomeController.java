package com.proyecto.tienda.controller;

import com.proyecto.tienda.model.Juego;
import com.proyecto.tienda.repository.JuegoRepository;
import com.proyecto.tienda.repository.PlataformaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private JuegoRepository juegoRepository;

    @Autowired
    private PlataformaRepository plataformaRepository;

    @GetMapping("/")
    public String inicio(Model model) {
        // Obtenemos los últimos 4 juegos de la base de datos
        List<Juego> ultimosJuegos = juegoRepository.findTop4ByOrderByIdDesc();
        
        // Los enviamos a la vista
        model.addAttribute("ultimosJuegos", ultimosJuegos);
        model.addAttribute("plataformas", plataformaRepository.findAll(Sort.by("nombre")));
        
        return "Home/home"; // Esto buscará el archivo templates/index.html
    }
}
