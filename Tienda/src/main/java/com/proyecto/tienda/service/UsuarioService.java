package com.proyecto.tienda.service;

import com.proyecto.tienda.dto.UsuarioRegistroDTO;
import com.proyecto.tienda.dto.UsuarioAdminFormDTO;
import com.proyecto.tienda.exception.CampoInvalidoException;
import com.proyecto.tienda.exception.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.tienda.model.Rol;
import com.proyecto.tienda.model.Usuario;
import com.proyecto.tienda.repository.RolRepository;
import com.proyecto.tienda.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// --- NUEVAS IMPORTACIONES PARA EL PANEL DE ADMIN ---
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Service
@Validated
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registrarUsuario(@Valid UsuarioRegistroDTO dto) {
        // 1. Validar que las contraseñas coincidan
        if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
            throw new CampoInvalidoException("confirmarPassword", "Las contraseñas no coinciden.");
        }

        // 2. Validar que el correo no exista
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new CampoInvalidoException("email", "El correo electrónico ya está registrado.");
        }

        // 3. Crear y poblar la entidad
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 4. Asignar el rol USER por defecto
        Rol rolUser = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Error crítico: Rol USER no encontrado en la BD."));

        nuevoUsuario.setRol(rolUser);

        // 5. Guardar en BD
        usuarioRepository.saveAndFlush(nuevoUsuario);
    }

    // ==========================================
    // MÉTODOS DE ADMINISTRACIÓN (CRUD Y SEGURIDAD)
    // ==========================================

    public Page<Usuario> listarUsuariosPaginados(String buscar, Pageable pageable) {
        if (buscar != null && !buscar.trim().isEmpty()) {
            // Requiere que agregues este método en tu UsuarioRepository
            return usuarioRepository.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(buscar, buscar,
                    pageable);
        }
        return usuarioRepository.findAll(pageable);
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario solicitado no existe."));
    }

    @Transactional
    public Usuario actualizarUsuarioAdmin(Long id, @Valid UsuarioAdminFormDTO dto) {
        String nombre = dto.getNombre();
        String email = dto.getEmail();
        Long rolId = dto.getRolId();
        Usuario usuario = obtenerUsuarioPorId(id);

        // Validar si el correo nuevo ya lo usa otra persona distinta
        usuarioRepository.findByEmail(email).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new CampoInvalidoException("email", "El correo ya está en uso por otro usuario.");
            }
        });

        Rol rolNuevo = rolRepository.findById(rolId)
                .orElseThrow(() -> new CampoInvalidoException("rolId", "Selecciona un rol válido."));

        String correoActual = obtenerCorreoUsuarioAutenticado();
        boolean esElMismoUsuario = usuario.getEmail().strip().equalsIgnoreCase(correoActual);
        boolean eraAdmin = usuario.getRol().getNombre().equals("ADMIN");
        boolean seraAdmin = rolNuevo.getNombre().equals("ADMIN");

        // GUARDA 1: Un admin no puede cambiar su propio rol
        if (esElMismoUsuario && eraAdmin && !seraAdmin) {
            throw new CampoInvalidoException("rolId", "No puedes quitarte tu propio rol de Administrador.");
        }

        // GUARDA 2: No dejar el sistema sin administradores
        if (eraAdmin && !seraAdmin) {
            long totalAdmins = usuarioRepository.countByRolNombre("ADMIN");
            if (totalAdmins <= 1) {
                throw new CampoInvalidoException("rolId", "No puedes quitar el rol al último administrador del sistema.");
            }
        }

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setRol(rolNuevo);

        return usuarioRepository.saveAndFlush(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = obtenerUsuarioPorId(id);

        // GUARDA 1: Evitar auto-eliminación
        String correoActual = obtenerCorreoUsuarioAutenticado();
        if (usuario.getEmail().strip().equalsIgnoreCase(correoActual)) {
            throw new IllegalStateException("Seguridad: No puedes eliminar tu propia cuenta.");
        }

        // GUARDA 2: Evitar eliminar al último administrador
        if (usuario.getRol().getNombre().equals("ADMIN")) {
            long totalAdmins = usuarioRepository.countByRolNombre("ADMIN");
            if (totalAdmins <= 1) {
                throw new IllegalStateException(
                        "Riesgo Crítico: No puedes eliminar a este usuario porque es el último Administrador del sistema.");
            }
        }

        usuarioRepository.delete(usuario);
        usuarioRepository.flush();
    }

    // Método auxiliar para saber quién está interactuando con el sistema
    private String obtenerCorreoUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}