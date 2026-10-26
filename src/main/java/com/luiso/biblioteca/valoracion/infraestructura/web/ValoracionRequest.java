package com.luiso.biblioteca.valoracion.infraestructura.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de entrada web. Sin "socio": se toma del token (paso 6.5)
public record ValoracionRequest(

        @NotNull(message = "La puntuación es obligatoria")
        @Min(value = 1, message = "La puntuación mínima es 1")
        @Max(value = 5, message = "La puntuación máxima es 5")
        Integer puntuacion,

        @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
        String comentario
) {}