package com.luiso.biblioteca.valoracion.dominio;

import java.time.LocalDateTime;

// Modelo de DOMINIO. Fíjate en los imports: solo java.time.
// Ni Spring ni JPA. Se podría usar en cualquier aplicación Java.
public record Valoracion(
        Long id,
        Long libroId,
        String socio,
        int puntuacion,
        String comentario,
        LocalDateTime fecha
) {

    // Constructor compacto de un record: se ejecuta al crear el objeto.
    // Aquí viven las REGLAS DEL DOMINIO: es imposible crear una Valoracion inválida
    public Valoracion {
        if (puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }
        if (comentario != null && comentario.length() > 500) {
            throw new IllegalArgumentException("El comentario no puede superar los 500 caracteres");
        }
    }

    // Método de fábrica con nombre de negocio: crear una valoración nueva (sin id todavía)
    public static Valoracion nueva(Long libroId, String socio, int puntuacion,
                                   String comentario, LocalDateTime fecha) {
        return new Valoracion(null, libroId, socio, puntuacion, comentario, fecha);
    }
}