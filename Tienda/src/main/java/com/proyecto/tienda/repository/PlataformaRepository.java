package com.proyecto.tienda.repository;

import com.proyecto.tienda.model.Plataforma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {

    // Para el buscador del panel de administración
    Page<Plataforma> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Para evitar registrar "PlayStation 5" dos veces
    boolean existsByNombreIgnoreCase(String nombre);
    @Query("select (count(e) > 0) from Plataforma e where lower(trim(e.nombre)) = lower(:nombre) and (:id is null or e.id <> :id)")
    boolean existeNombre(@Param("nombre") String nombre, @Param("id") Long id);
}