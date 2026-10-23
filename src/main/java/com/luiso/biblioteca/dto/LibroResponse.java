package com.luiso.biblioteca.dto;

import com.luiso.biblioteca.entity.Libro;

public record LibroResponse(Long id, String titulo, String autor, int paginas) {

    // Convierte la entidad (interna) en el DTO (lo que ve el cliente)
    public static LibroResponse desde(Libro libro) {
        return new LibroResponse(libro.getId(), libro.getTitulo(), libro.getAutor(), libro.getPaginas());
    }
}