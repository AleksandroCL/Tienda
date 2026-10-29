package com.proyecto.tienda.dto;

import java.math.BigDecimal;

public record DashboardJuegoDTO(Long id, String titulo, String imagenUrl,
        BigDecimal precio, int stock, String plataformas) { }
