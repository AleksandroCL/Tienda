package com.proyecto.tienda.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    
    private String nombre;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String nombre) {
        super(username, password, authorities);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}