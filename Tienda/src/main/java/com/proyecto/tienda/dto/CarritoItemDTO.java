package com.proyecto.tienda.dto;

import java.math.BigDecimal;

public class CarritoItemDTO {

    private Long juegoId;
    private String titulo;
    private String imagenUrl;
    private BigDecimal precio;
    private Integer cantidad;
    private Integer stock;

    public CarritoItemDTO() {
    }

    public CarritoItemDTO(
            Long juegoId,
            String titulo,
            String imagenUrl,
            BigDecimal precio,
            Integer cantidad,
            Integer stock) {

        this.juegoId = juegoId;
        this.titulo = titulo;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.cantidad = cantidad;
        this.stock = stock;
    }

    public Long getJuegoId() {
        return juegoId;
    }

    public void setJuegoId(Long juegoId) {
        this.juegoId = juegoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getSubtotal() {
        if (precio == null || cantidad == null) {
            return BigDecimal.ZERO;
        }

        return precio.multiply(BigDecimal.valueOf(cantidad));
    }
}