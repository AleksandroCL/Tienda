package com.proyecto.tienda.controller;

import com.proyecto.tienda.dto.UsuarioAdminFormDTO;
import com.proyecto.tienda.exception.CampoInvalidoException;
import com.proyecto.tienda.model.Usuario;
import com.proyecto.tienda.repository.RolRepository;
import com.proyecto.tienda.service.CustomUserDetails;
import com.proyecto.tienda.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {
    private final UsuarioService usuarios;
    private final RolRepository roles;

    public AdminUsuarioController(UsuarioService usuarios, RolRepository roles) {
        this.usuarios = usuarios;
        this.roles = roles;
    }

    @GetMapping
    public String listarUsuarios(@RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page, Model model) {
        page = Math.max(0, page);
        var resultado = usuarios.listarUsuariosPaginados(buscar, PageRequest.of(page, 10));
        model.addAttribute("usuarios", resultado.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("buscarActual", buscar);
        return "admin/usuarios/usuario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarios.obtenerUsuarioPorId(id);
        UsuarioAdminFormDTO dto = new UsuarioAdminFormDTO();
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRolId(usuario.getRol().getId());
        model.addAttribute("usuarioDto", dto);
        return formulario(id, model);
    }

    @PostMapping("/editar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
            @Valid @ModelAttribute("usuarioDto") UsuarioAdminFormDTO dto, BindingResult result,
            Model model, RedirectAttributes redirect, HttpServletRequest request, HttpServletResponse response) {
        String correoAnterior = usuarios.obtenerUsuarioPorId(id).getEmail();
        if (!result.hasErrors()) {
            try {
                Usuario actualizado = usuarios.actualizarUsuarioAdmin(id, dto);
                refrescarSesionPropia(correoAnterior, actualizado, request, response);
                redirect.addFlashAttribute("exito", "Usuario actualizado correctamente.");
                return "redirect:/admin/usuarios";
            } catch (CampoInvalidoException e) {
                result.rejectValue(e.getCampo(), "invalido", e.getMessage());
            } catch (DataIntegrityViolationException e) {
                result.reject("guardar", "No se pudo guardar. Comprueba que el correo no esté en uso y el rol siga disponible.");
            }
        }
        return formulario(id, model);
    }

    private String formulario(Long id, Model model) {
        model.addAttribute("usuarioId", id);
        model.addAttribute("roles", roles.findAll());
        return "admin/usuarios/usuario-form";
    }

    private void refrescarSesionPropia(String correoAnterior, Usuario usuario,
            HttpServletRequest request, HttpServletResponse response) {
        var actual = SecurityContextHolder.getContext().getAuthentication();
        if (!correoAnterior.strip().equalsIgnoreCase(actual.getName())) return;
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre()));
        var principal = new CustomUserDetails(usuario.getEmail(), usuario.getPassword(), authorities, usuario.getNombre());
        var authentication = UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities);
        authentication.setDetails(actual.getDetails());
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        new HttpSessionSecurityContextRepository().saveContext(context, request, response);
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            usuarios.eliminarUsuario(id);
            redirect.addFlashAttribute("exito", "Usuario eliminado correctamente.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error", "No se puede eliminar un usuario con registros relacionados.");
        }
        return "redirect:/admin/usuarios";
    }
}
