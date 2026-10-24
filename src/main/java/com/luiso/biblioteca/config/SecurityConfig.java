package com.luiso.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Definimos NUESTRA cadena de filtros. Al existir este bean,
    // Spring Boot deja de aplicar su configuración por defecto (paso 2.4: "tú mandas")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. AUTORIZACIÓN: reglas por URL. Se evalúan EN ORDEN, y gana la primera que encaja
                .authorizeHttpRequests(auth -> auth
                        // Consultar libros: público
                        .requestMatchers(HttpMethod.GET, "/api/libros/**").permitAll()
                        // Documentación: pública
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Página de error interna de Spring Boot: pública (ver la explicación abajo)
                        .requestMatchers("/error").permitAll()
                        // Todo lo demás: hay que estar autenticado
                        .anyRequest().authenticated())

                // 2. AUTENTICACIÓN: usuario y contraseña en la cabecera Authorization (HTTP Basic)
                .httpBasic(Customizer.withDefaults())

                // 3. API sin estado: no se crea sesión. Cada petición trae sus credenciales
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. CSRF desactivado. Es seguro en una API sin estado que no usa cookies
                //    (lo explico a fondo en el paso 6.5)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}