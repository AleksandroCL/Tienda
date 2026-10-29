package com.proyecto.tienda.dto;

import com.proyecto.tienda.validation.ContrasenaValida;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Locale;

@Getter
@Setter
public class UsuarioRegistroDTO {
    @NotBlank(message = "Escribe tu nombre.")
    @Size(max = 100, message = "El nombre admite hasta 100 caracteres.")
    private String nombre;

    @NotBlank(message = "Escribe tu correo electrónico.")
    @Email(message = "Escribe un correo electrónico válido.")
    @Size(max = 150, message = "El correo admite hasta 150 caracteres.")
    private String email;

    @ContrasenaValida
    private String password;

    @NotBlank(message = "Confirma tu contraseña.")
    private String confirmarPassword;

    public void setNombre(String nombre) { this.nombre = nombre == null ? null : nombre.strip(); }
    public void setEmail(String email) { this.email = email == null ? null : email.strip().toLowerCase(Locale.ROOT); }
}
