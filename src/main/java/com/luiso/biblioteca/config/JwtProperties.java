package com.luiso.biblioteca.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// @Validated: Spring valida estas propiedades AL ARRANCAR.
// Si falta la clave secreta, la aplicación no arranca (mejor que fallar en el primer login)
@Validated
@ConfigurationProperties(prefix = "biblioteca.jwt")
public record JwtProperties(
        @NotBlank String secreto,     // clave en Base64
        @NotBlank String emisor,      // valor del claim "iss"
        @NotNull Duration expiracion,  // Duration: Spring convierte "30m" en 30 minutos
        @NotNull Duration expiracionRefresh   // nuevo
) {}