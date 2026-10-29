package com.proyecto.tienda.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Locale;

@Getter
@Setter
public class UsuarioAdminFormDTO {
    @NotBlank(message = "Escribe el nombre.")
    @Size(max = 100, message = "El nombre admite hasta 100 caracteres.")
    private String nombre;

    @NotBlank(message = "Escribe el correo electrónico.")
    @Email(message = "Escribe un correo electrónico válido.")
    @Size(max = 150, message = "El correo admite hasta 150 caracteres.")
    private String email;

    @NotNull(message = "Selecciona un rol.")
    @Positive(message = "Selecciona un rol válido.")
    private Long rolId;

    public void setNombre(String nombre) { this.nombre = nombre == null ? null : nombre.strip(); }
    public void setEmail(String email) { this.email = email == null ? null : email.strip().toLowerCase(Locale.ROOT); }
}
