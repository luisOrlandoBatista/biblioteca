package com.luiso.biblioteca.event;

import java.time.LocalDate;

// Un evento describe algo que YA ha pasado: nombre en pasado.
// Es un record: inmutable, nadie puede cambiarlo mientras viaja a los listeners
public record PrestamoCreadoEvent(
        Long prestamoId,
        Long libroId,
        String tituloLibro,
        String socio,
        LocalDate fechaPrestamo
) {}