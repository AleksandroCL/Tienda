package com.proyecto.tienda.service;

import com.proyecto.tienda.dto.ConsultaSoporteDTO;
import com.proyecto.tienda.model.ConsultaSoporte;
import com.proyecto.tienda.repository.ConsultaSoporteRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class SoporteService {
    private final ConsultaSoporteRepository consultas;

    public SoporteService(ConsultaSoporteRepository consultas) { this.consultas = consultas; }

    @Transactional
    public Long registrar(@Valid ConsultaSoporteDTO dto) {
        ConsultaSoporte consulta = new ConsultaSoporte();
        consulta.setNombre(dto.getNombre());
        consulta.setEmail(dto.getEmail());
        consulta.setAsunto(dto.getAsunto());
        consulta.setMensaje(dto.getMensaje());
        return consultas.saveAndFlush(consulta).getId();
    }

    @Transactional(readOnly = true)
    public Page<ConsultaSoporte> listar(int page) {
        return consultas.findAll(PageRequest.of(Math.max(0, page), 10,
                Sort.by(Sort.Direction.DESC, "fechaCreacion", "id")));
    }
}
