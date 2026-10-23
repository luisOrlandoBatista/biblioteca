package com.luiso.biblioteca.model;

// El modelo representa los datos de nuestra aplicación.
// No es un bean: es un simple objeto de datos, por eso no lleva anotación.
public record Libro(Long id, String titulo, String autor, int paginas) {}
