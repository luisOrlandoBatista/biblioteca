package com.luiso.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Sin campo "rol": el cliente NO puede elegirlo (lo mismo que hicimos con el id en el paso 3.2)
public record RegistroRequest(

        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "[a-zA-Z0-9._-]+",
                 message = "El nombre de usuario solo puede contener letras, números, puntos, guiones y guiones bajos")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password
) {}