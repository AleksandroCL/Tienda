package com.proyecto.tienda.dto;

import java.util.List;

public record DashboardResumenDTO(long totalJuegos, long totalUsuarios,
        long totalCategorias, long totalPlataformas, long juegosAgotados,
        long juegosStockBajo, long unidadesDisponibles, int umbralStockBajo,
        List<DashboardJuegoDTO> juegosAtencion, List<DashboardJuegoDTO> ultimosJuegos) { }
