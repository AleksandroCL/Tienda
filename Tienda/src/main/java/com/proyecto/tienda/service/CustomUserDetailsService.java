package com.proyecto.tienda.service;

import com.proyecto.tienda.model.Usuario;
import com.proyecto.tienda.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Buscamos al usuario en MySQL usando el email ingresado en el login
        Usuario usuario = usuarioRepository.findByEmail(email.strip())
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró una cuenta con el correo: " + email));

        // 2. Extraemos su Rol y le agregamos el prefijo "ROLE_" (Regla obligatoria de Spring Security)
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre());

        // 3. Retornamos tu clase CustomUserDetails
        // Esto le da a Spring la contraseña encriptada para compararla y, además, guarda el Nombre del usuario.
        return new CustomUserDetails(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.singletonList(authority),
                usuario.getNombre()
        );
    }
}