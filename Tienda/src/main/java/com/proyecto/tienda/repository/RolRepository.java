package com.proyecto.tienda.repository;

import com.proyecto.tienda.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    
    // Spring Data JPA crea la consulta SQL automáticamente gracias a este nombre
    Optional<Rol> findByNombre(String nombre);
    
}