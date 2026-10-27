package com.luiso.biblioteca.dto;

import java.time.LocalDate;

public record PrestamoResponse(
        Long id,
        Long libroId,
        String tituloLibro,
        String socio,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        boolean activo
) {}