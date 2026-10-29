package com.proyecto.tienda.repository;

import com.proyecto.tienda.model.Juego;
import com.proyecto.tienda.dto.InventarioResumen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {

        // Consulta personalizada para filtrar (búsqueda + categoría + plataforma),
        // ahora paginada. Se especifica countQuery aparte porque el DISTINCT + JOIN
        // puede confundir al conteo automático de Spring Data.
        @Query(value = "SELECT DISTINCT j FROM Juego j " +
                        "LEFT JOIN j.categorias c " +
                        "LEFT JOIN j.plataformas p " +
                        "WHERE (:buscar IS NULL OR :buscar = '' OR LOWER(j.titulo) LIKE LOWER(CONCAT('%', :buscar, '%'))) "
                        +
                        "AND (:categoriaId IS NULL OR c.id = :categoriaId) " +
                        "AND (:plataformaId IS NULL OR p.id = :plataformaId)", countQuery = "SELECT COUNT(DISTINCT j) FROM Juego j "
                                        +
                                        "LEFT JOIN j.categorias c " +
                                        "LEFT JOIN j.plataformas p " +
                                        "WHERE (:buscar IS NULL OR :buscar = '' OR LOWER(j.titulo) LIKE LOWER(CONCAT('%', :buscar, '%'))) "
                                        +
                                        "AND (:categoriaId IS NULL OR c.id = :categoriaId) " +
                                        "AND (:plataformaId IS NULL OR p.id = :plataformaId)")
        Page<Juego> filtrarJuegos(@Param("buscar") String buscar,
                        @Param("categoriaId") Long categoriaId,
                        @Param("plataformaId") Long plataformaId,
                        Pageable pageable);

        // Usado por CategoriaController para impedir borrar una categoría que
        // todavía tiene juegos asociados (evita un error de integridad referencial)
        boolean existsByCategorias_Id(Long categoriaId);

        boolean existsByPlataformas_Id(Long plataformaId);

        // NUEVO: Trae los 4 últimos juegos basándose en el ID (los más recientes) para
        // la página de inicio
        List<Juego> findTop4ByOrderByIdDesc();

        @Query("""
                SELECT COUNT(j) AS totalJuegos,
                       COALESCE(SUM(CASE WHEN j.stock IS NULL OR j.stock <= 0 THEN 1 ELSE 0 END), 0) AS juegosAgotados,
                       COALESCE(SUM(CASE WHEN j.stock BETWEEN 1 AND :umbral THEN 1 ELSE 0 END), 0) AS juegosStockBajo,
                       COALESCE(SUM(CASE WHEN j.stock > 0 THEN j.stock ELSE 0 END), 0) AS unidadesDisponibles
                FROM Juego j
                """)
        InventarioResumen resumirInventario(@Param("umbral") int umbral);

        @Query("""
                SELECT j FROM Juego j WHERE COALESCE(j.stock, 0) <= :umbral
                ORDER BY COALESCE(j.stock, 0) ASC, j.titulo ASC, j.id ASC
                """)
        List<Juego> buscarConPocoStock(@Param("umbral") int umbral, Pageable limite);

        List<Juego> findTop5ByOrderByIdDesc();

        @EntityGraph(attributePaths = "plataformas")
        @Query("SELECT DISTINCT j FROM Juego j WHERE j.id IN :ids ORDER BY j.id DESC")
        List<Juego> buscarParaDashboard(@Param("ids") List<Long> ids);
}