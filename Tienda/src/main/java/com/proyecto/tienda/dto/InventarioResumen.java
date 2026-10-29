package com.proyecto.tienda.dto;

/** Proyección de los indicadores de inventario, calculados en MySQL. */
public interface InventarioResumen {
    long getTotalJuegos();
    long getJuegosAgotados();
    long getJuegosStockBajo();
    long getUnidadesDisponibles();
}
