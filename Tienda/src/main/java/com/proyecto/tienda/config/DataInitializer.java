package com.proyecto.tienda.config;

import com.proyecto.tienda.model.Categoria;
import com.proyecto.tienda.model.Plataforma;
import com.proyecto.tienda.model.Rol;
import com.proyecto.tienda.model.Usuario;
import com.proyecto.tienda.repository.CategoriaRepository;
import com.proyecto.tienda.repository.PlataformaRepository;
import com.proyecto.tienda.repository.RolRepository;
import com.proyecto.tienda.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import com.proyecto.tienda.validation.PasswordPolicy;
import java.util.Locale;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            CategoriaRepository categoriaRepository,
            PlataformaRepository plataformaRepository,
            @Value("${app.bootstrap.admin.email:}") String adminEmail,
            @Value("${app.bootstrap.admin.password:}") String adminPassword,
            @Value("${app.bootstrap.admin.name:Administrador}") String adminName) {
        return args -> {

            // Crear los roles antes del administrador para permitir una BD vacía.
            Rol rolAdmin = obtenerOCrearRol(rolRepository, "ADMIN");
            obtenerOCrearRol(rolRepository, "USER");

            // El arranque nunca modifica una cuenta existente.
            crearAdministradorSiCorresponde(usuarioRepository, passwordEncoder,
                    rolAdmin, adminEmail, adminPassword, adminName);

            // 2. INICIALIZAR CATEGORÍAS (Si la tabla está vacía)
            if (categoriaRepository.count() == 0) {
                List<String> nombresCategorias = Arrays.asList(
                        "Acción", "Aventura", "RPG", "Shooter", "Deportes", "Estrategia", "Terror");

                for (String nombre : nombresCategorias) {
                    Categoria categoria = new Categoria();
                    categoria.setNombre(nombre);
                    categoriaRepository.save(categoria);
                }
                System.out.println("✅ Categorías iniciales creadas en la base de datos.");
            }

            // 3. INICIALIZAR PLATAFORMAS (Si la tabla está vacía)
            if (plataformaRepository.count() == 0) {
                List<String> nombresPlataformas = Arrays.asList(
                        "PC", "PlayStation 5", "PlayStation 4", "Xbox Series X/S", "Xbox One", "Nintendo Switch");

                for (String nombre : nombresPlataformas) {
                    Plataforma plataforma = new Plataforma();
                    plataforma.setNombre(nombre);
                    plataformaRepository.save(plataforma);
                }
                System.out.println("✅ Plataformas iniciales creadas en la base de datos.");
            }
        };
    }

    private Rol obtenerOCrearRol(RolRepository repository, String nombre) {
        return repository.findByNombre(nombre)
                .orElseGet(() -> repository.save(Rol.builder().nombre(nombre).build()));
    }

    private void crearAdministradorSiCorresponde(UsuarioRepository repository,
            PasswordEncoder passwordEncoder, Rol rolAdmin,
            String email, String password, String nombre) {
        if (email.isBlank()) {
            if (!password.isEmpty()) {
                throw new IllegalStateException("Configura ADMIN_EMAIL junto con ADMIN_PASSWORD.");
            }
            return;
        }

        String correo = email.strip().toLowerCase(Locale.ROOT);
        if (repository.findByEmail(correo).isPresent()) {
            return;
        }

        if (!PasswordPolicy.isValid(password)) {
            throw new IllegalStateException("ADMIN_PASSWORD: " + PasswordPolicy.MESSAGE);
        }
        if (nombre.isBlank() || nombre.length() > 100 || correo.length() > 150
                || !correo.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalStateException("Revisa ADMIN_EMAIL y ADMIN_NAME: deben ser un correo y un nombre válidos.");
        }

        Usuario admin = new Usuario();
        admin.setNombre(nombre.trim());
        admin.setEmail(correo);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRol(rolAdmin);
        repository.save(admin);
    }
}
