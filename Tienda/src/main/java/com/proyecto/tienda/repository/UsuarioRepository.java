package com.proyecto.tienda.repository;

import com.proyecto.tienda.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select u from Usuario u where lower(trim(u.email)) = lower(:email)")
    Optional<Usuario> findByEmail(@Param("email") String email);

    // NUEVO: Búsqueda paginada ignorando mayúsculas/minúsculas
    Page<Usuario> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombre, String email,
            Pageable pageable);

    // Cuenta cuántos usuarios tienen un rol específico
    long countByRolNombre(String nombre);
}