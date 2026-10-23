package com.luiso.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PrestamoRequest(

        @NotNull(message = "El id del libro es obligatorio")
        @Positive(message = "El id del libro debe ser mayor que 0")
        Long libroId,

        @NotBlank(message = "El socio es obligatorio")
        @Size(max = 100, message = "El socio no puede superar los 100 caracteres")
        String socio
) {}