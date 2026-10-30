package com.luiso.biblioteca.panel;

import java.time.LocalDate;

// Lo que verá el bibliotecario en el panel. Un DTO propio, como siempre (paso 3.2)
public record PrestamoEnVivo(Long prestamoId, String tituloLibro, String socio, LocalDate fecha) {}