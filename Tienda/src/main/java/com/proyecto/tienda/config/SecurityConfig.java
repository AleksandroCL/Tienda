package com.proyecto.tienda.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                // ==========================================
                                // AUTORIZACIÓN
                                // ==========================================
                                .authorizeHttpRequests(auth -> auth

                                                // Permitir el manejo interno de errores (forward a /error)
                                                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                                                // Endpoint de errores de Spring Boot
                                                .requestMatchers("/error").permitAll()

                                                // Archivos estáticos
                                                .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()

                                                // ==========================================
                                                // RUTAS EXCLUSIVAS PARA ADMIN
                                                // Van antes que las públicas: "/juegos/nuevo" también
                                                // calzaría en el patrón de un solo segmento "/juegos/{id}"
                                                // si ese fuera evaluado primero.
                                                // ==========================================
                                                .requestMatchers(
                                                                "/juegos/nuevo",
                                                                "/juegos/editar/**",
                                                                "/juegos/eliminar/**",
                                                                "/admin/**")
                                                .hasRole("ADMIN")

                                                // ==========================================
                                                // RUTAS PÚBLICAS explícitas (catálogo y auth)
                                                // ==========================================
                                                .requestMatchers(
                                                                "/",
                                                                "/juegos",
                                                                "/juegos/{id}",
                                                                "/auth/login",
                                                                "/auth/registro",
                                                                "/soporte/terminos",
                                                                "/soporte/privacidad",
                                                                "/soporte/ayuda",
                                                                "/soporte/consulta")
                                                .permitAll()

                                                // ==========================================
                                                // TODO LO DEMÁS requiere estar autenticado
                                                // (seguro por defecto: si agregas una ruta nueva y
                                                // olvidas protegerla aquí, pide login en vez de
                                                // quedar pública sin querer)
                                                // ==========================================
                                                .anyRequest().authenticated())

                                // ==========================================
                                // MANEJO DE EXCEPCIONES
                                // ==========================================
                                .exceptionHandling(exception -> exception
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        response.sendError(403);
                                                }))

                                // ==========================================
                                // LOGIN
                                // ==========================================
                                .formLogin(form -> form
                                                .loginPage("/auth/login")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("email") // <--- Obligatorio que diga "email"
                                                .defaultSuccessUrl("/juegos", true)
                                                .permitAll())
                                // ==========================================
                                // LOGOUT
                                // ==========================================
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/auth/login?logout")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}