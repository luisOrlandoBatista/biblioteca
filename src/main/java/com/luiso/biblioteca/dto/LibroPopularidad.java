package com.luiso.biblioteca.dto;

// Long y no long: COUNT devuelve un Long, y el tipo debe coincidir con el del constructor
public record LibroPopularidad(String titulo, Long totalPrestamos) {}