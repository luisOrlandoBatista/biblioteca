package com.luiso.biblioteca.valoracion.infraestructura.web;

import java.time.LocalDateTime;

import com.luiso.biblioteca.valoracion.dominio.Valoracion;

public record ValoracionResponse(Long id, Long libroId, String socio,
                                 int puntuacion, String comentario, LocalDateTime fecha) {

    static ValoracionResponse desde(Valoracion v) {
        return new ValoracionResponse(v.id(), v.libroId(), v.socio(), v.puntuacion(), v.comentario(), v.fecha());
    }
}