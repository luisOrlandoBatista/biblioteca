package com.luiso.biblioteca.mensajeria;

import java.time.LocalDate;

// El CONTRATO que leerán otros sistemas.
// "version": si algún día cambia el formato, los consumidores sabrán qué versión reciben
public record PrestamoMensaje(
        int version,
        Long prestamoId,
        Long libroId,
        String tituloLibro,
        String socio,
        LocalDate fechaPrestamo
) {}