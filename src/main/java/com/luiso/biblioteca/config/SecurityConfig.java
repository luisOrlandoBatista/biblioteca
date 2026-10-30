package com.luiso.biblioteca.config;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import jakarta.servlet.DispatcherType;

@Configuration
@EnableMethodSecurity // activa @PreAuthorize en los métodos de los beans
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {

        AuthenticationEntryPoint noAutenticado =
                (request, response, ex) -> resolver.resolveException(request, response, null, ex);
        AccessDeniedHandler sinPermiso =
                (request, response, ex) -> resolver.resolveException(request, response, null, ex);

        http
                // CORS: usa el bean corsConfigurationSource de más abajo (Parte 2)
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // Despachos internos de Spring (continuación de respuestas asíncronas y errores):
                        // la petición original ya pasó la seguridad
                        .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.ERROR).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/login", "/api/auth/registro",
                                "/api/auth/refresh", "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/libros/**", "/api/catalogo-externo").permitAll()

                        // Valorar: cualquier socio o admin. DEBE ir antes de la regla de ADMIN
                        // "*" representa UN segmento de la ruta: el id del libro
                        .requestMatchers(HttpMethod.POST, "/api/libros/*/valoraciones").hasAnyRole("SOCIO", "ADMIN")
                        
                        // NUEVO: escribir en el catálogo es solo para ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/libros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/libros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/libros/**").hasRole("ADMIN")

                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint(noAutenticado))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(noAutenticado)
                        .accessDeniedHandler(sinPermiso))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable()); // explicado en la Parte 3

        return http.build();
    }

    // CORS (Parte 2)
    @Bean
    public CorsConfigurationSource corsConfigurationSource(CorsProperties properties) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(properties.origenesPermitidos()); // qué webs pueden llamar a la API
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type")); // cabeceras que puede enviar
        config.setExposedHeaders(List.of("Location"));    // cabeceras de la respuesta que puede leer
        config.setMaxAge(Duration.ofHours(1));             // el navegador recuerda la respuesta 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config); // solo para las rutas de la API
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}