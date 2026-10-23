package com.luiso.biblioteca.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para crear o modificar un libro")
public record LibroRequest(

        @Schema(description = "Título del libro", example = "Refactoring")
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede superar los 200 caracteres")
        String titulo,

        @Schema(description = "Nombre del autor", example = "Martin Fowler")
        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 100, message = "El autor no puede superar los 100 caracteres")
        String autor,

        @Schema(description = "Número de páginas", example = "448")
        @NotNull(message = "El número de páginas es obligatorio")
        @Positive(message = "El número de páginas debe ser mayor que 0")
        Integer paginas
) {}