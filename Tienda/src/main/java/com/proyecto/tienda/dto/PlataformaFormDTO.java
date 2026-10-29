package com.proyecto.tienda.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlataformaFormDTO {
    @NotBlank(message = "Escribe el nombre de la plataforma.")
    @Size(max = 30, message = "La plataforma admite hasta 30 caracteres.")
    private String nombre;

    public void setNombre(String nombre) { this.nombre = nombre == null ? null : nombre.strip(); }
}
