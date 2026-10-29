package com.proyecto.tienda.controller;

import com.proyecto.tienda.dto.UsuarioRegistroDTO;
import com.proyecto.tienda.exception.CampoInvalidoException;
import com.proyecto.tienda.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioService usuarios;
    public AuthController(UsuarioService usuarios) { this.usuarios = usuarios; }

    @GetMapping("/login")
    public String mostrarLogin() { return "auth/login"; }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioRegistroDTO());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("usuarioDTO") UsuarioRegistroDTO dto, BindingResult result) {
        if (!result.hasFieldErrors("confirmarPassword") && !Objects.equals(dto.getPassword(), dto.getConfirmarPassword())) {
            result.rejectValue("confirmarPassword", "coincidencia", "Las contraseñas no coinciden.");
        }
        if (!result.hasErrors()) {
            try {
                usuarios.registrarUsuario(dto);
                return "redirect:/auth/login?exito";
            } catch (CampoInvalidoException e) {
                result.rejectValue(e.getCampo(), "invalido", e.getMessage());
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("email", "duplicado", "No se pudo registrar ese correo. Comprueba si ya tienes una cuenta.");
            }
        }
        dto.setPassword(null);
        dto.setConfirmarPassword(null);
        return "auth/registro";
    }
}
