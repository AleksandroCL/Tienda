package com.proyecto.tienda.service;

import com.proyecto.tienda.dto.PlataformaFormDTO;
import com.proyecto.tienda.exception.CampoInvalidoException;
import com.proyecto.tienda.exception.RecursoNoEncontradoException;
import com.proyecto.tienda.model.Plataforma;
import com.proyecto.tienda.repository.PlataformaRepository;
import com.proyecto.tienda.repository.JuegoRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class PlataformaService {
    private final PlataformaRepository plataformas;
    private final JuegoRepository juegos;

    public PlataformaService(PlataformaRepository plataformas, JuegoRepository juegos) {
        this.plataformas = plataformas;
        this.juegos = juegos;
    }

    public Page<Plataforma> listarPlataformasPaginadas(String buscar, Pageable pageable) {
        return buscar != null && !buscar.isBlank()
                ? plataformas.findByNombreContainingIgnoreCase(buscar.strip(), pageable)
                : plataformas.findAll(pageable);
    }

    public Plataforma obtenerPlataformaPorId(Long id) {
        return plataformas.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("La plataforma solicitada no existe."));
    }

    @Transactional
    public void guardar(Long id, @Valid PlataformaFormDTO dto) {
        Plataforma plataforma = id == null ? new Plataforma() : obtenerPlataformaPorId(id);
        if (plataformas.existeNombre(dto.getNombre(), id)) {
            throw new CampoInvalidoException("nombre", "Ya existe una plataforma con ese nombre.");
        }
        plataforma.setNombre(dto.getNombre());
        plataformas.saveAndFlush(plataforma);
    }

    @Transactional
    public void eliminarPlataforma(Long id) {
        Plataforma plataforma = obtenerPlataformaPorId(id);
        if (juegos.existsByPlataformas_Id(id)) {
            throw new IllegalStateException("Hay juegos que usan esta plataforma. Quítasela antes de eliminarla.");
        }
        plataformas.delete(plataforma);
        plataformas.flush();
    }
}
