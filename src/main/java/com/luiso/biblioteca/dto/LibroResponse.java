package com.luiso.biblioteca.dto;

import com.luiso.biblioteca.model.Libro;

public record LibroResponse(Long id, String titulo, String autor, int paginas) {

    // Método "fábrica": convierte un Libro (modelo interno) en un LibroResponse (DTO).
    // static: se llama sobre el tipo, sin crear objeto → LibroResponse.desde(libro)
    public static LibroResponse desde(Libro libro) {
        return new LibroResponse(libro.id(), libro.titulo(), libro.autor(), libro.paginas());
    }
}