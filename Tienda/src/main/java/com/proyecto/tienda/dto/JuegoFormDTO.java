package com.proyecto.tienda.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class JuegoFormDTO {

    private Long id; // Útil si luego usamos este mismo DTO para editar un juego

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no debe superar los 150 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    @Digits(integer = 8, fraction = 2, message = "Usa hasta 8 cifras enteras y 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    // AHORA ES UNA LISTA PARA SOPORTAR MÚLTIPLES CATEGORÍAS
    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    private List<@NotNull @Positive Long> categoriaIds;

    @NotNull(message = "Debe seleccionar una plataforma")
    @Positive(message = "Selecciona una plataforma válida")
    private Long plataformaId;

    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    private LocalDate fechaLanzamiento;

    @Size(max = 255, message = "La URL de la imagen es demasiado larga")
    private String imagenUrl;
}