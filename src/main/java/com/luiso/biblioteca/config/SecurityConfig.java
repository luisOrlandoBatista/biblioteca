package com.luiso.biblioteca.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            // El mecanismo de Spring MVC que usa tu GlobalExceptionHandler.
            // Hay varios beans de este tipo; @Qualifier elige el principal por su nombre
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {

        // Los errores de seguridad ocurren en los FILTROS, antes del controlador (paso 6.1),
        // así que tu GlobalExceptionHandler no los ve. Con esto se los REENVIAMOS,
        // para que también respondan con un ProblemDetail
        AuthenticationEntryPoint noAutenticado =
                (request, response, ex) -> resolver.resolveException(request, response, null, ex);
        AccessDeniedHandler sinPermiso =
                (request, response, ex) -> resolver.resolveException(request, response, null, ex);

        http
                .authorizeHttpRequests(auth -> auth
                        // Login y registro: públicos
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/registro",
                            "/api/auth/refresh", "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/libros/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())

                // Sustituye a httpBasic: ahora las peticiones se autentican con un JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint(noAutenticado))

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(noAutenticado) // 401
                        .accessDeniedHandler(sinPermiso))        // 403

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expone como bean el AuthenticationManager que Spring construye solo con tu
    // UserDetailsService y tu PasswordEncoder (lo viste en la explicación del flujo).
    // Lo necesitamos para comprobar usuario y contraseña en el endpoint de login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}