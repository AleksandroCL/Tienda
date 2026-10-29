package com.proyecto.tienda.repository;

import com.proyecto.tienda.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // No necesitas escribir nada aquí, JpaRepository ya trae findAll(), save(), etc.
    @Query("select (count(e) > 0) from Categoria e where lower(trim(e.nombre)) = lower(:nombre) and (:id is null or e.id <> :id)")
    boolean existeNombre(@Param("nombre") String nombre, @Param("id") Long id);
}