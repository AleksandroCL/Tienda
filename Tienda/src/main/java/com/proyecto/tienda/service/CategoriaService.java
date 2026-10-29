package com.proyecto.tienda.service;

import com.proyecto.tienda.dto.CategoriaFormDTO;
import com.proyecto.tienda.exception.CampoInvalidoException;
import com.proyecto.tienda.exception.RecursoNoEncontradoException;
import com.proyecto.tienda.model.Categoria;
import com.proyecto.tienda.repository.CategoriaRepository;
import com.proyecto.tienda.repository.JuegoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CategoriaService {
    private final CategoriaRepository categorias;
    private final JuegoRepository juegos;

    public CategoriaService(CategoriaRepository categorias, JuegoRepository juegos) {
        this.categorias = categorias;
        this.juegos = juegos;
    }

    public Categoria obtenerPorId(Long id) {
        return categorias.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("La categoría solicitada no existe."));
    }

    @Transactional
    public void guardar(Long id, @Valid CategoriaFormDTO dto) {
        Categoria categoria = id == null ? new Categoria() : obtenerPorId(id);
        if (categorias.existeNombre(dto.getNombre(), id)) {
            throw new CampoInvalidoException("nombre", "Ya existe una categoría con ese nombre.");
        }
        categoria.setNombre(dto.getNombre());
        categorias.saveAndFlush(categoria);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = obtenerPorId(id);
        if (juegos.existsByCategorias_Id(id)) {
            throw new IllegalStateException("Hay juegos que usan esta categoría. Quítasela antes de eliminarla.");
        }
        categorias.delete(categoria);
        categorias.flush();
    }
}
