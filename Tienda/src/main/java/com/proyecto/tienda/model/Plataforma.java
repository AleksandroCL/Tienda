package com.proyecto.tienda.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plataformas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String nombre; // Ej: "PC", "PS5", "Xbox Series X", "Switch"
}