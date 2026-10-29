package com.proyecto.tienda.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Locale;

@Getter
@Setter
public class ConsultaSoporteDTO {
    @NotBlank(message = "Escribe tu nombre.")
    @Size(max = 100, message = "El nombre admite hasta 100 caracteres.")
    private String nombre;

    @NotBlank(message = "Escribe tu correo electrónico.")
    @Email(message = "Escribe un correo electrónico válido.")
    @Size(max = 150, message = "El correo admite hasta 150 caracteres.")
    private String email;

    @NotBlank(message = "Escribe el asunto de tu consulta.")
    @Size(max = 120, message = "El asunto admite hasta 120 caracteres.")
    private String asunto;

    @NotBlank(message = "Describe tu consulta.")
    @Size(min = 10, max = 2000, message = "Escribe entre 10 y 2000 caracteres.")
    private String mensaje;

    public void setNombre(String value) { nombre = value == null ? null : value.strip(); }
    public void setEmail(String value) { email = value == null ? null : value.strip().toLowerCase(Locale.ROOT); }
    public void setAsunto(String value) { asunto = value == null ? null : value.strip(); }
    public void setMensaje(String value) { mensaje = value == null ? null : value.strip(); }
}
