package com.luiso.biblioteca.dto;

import java.time.LocalDate;

import com.luiso.biblioteca.entity.Prestamo;

public record PrestamoResponse(
        Long id,
        Long libroId,
        String tituloLibro,
        String socio,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        boolean activo
) {

    public static PrestamoResponse desde(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getLibro().getId(),
                prestamo.getLibro().getTitulo(), // ⚠️ aquí se esconde el N+1 (lo verás enseguida)
                prestamo.getSocio(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.estaActivo());
    }
}