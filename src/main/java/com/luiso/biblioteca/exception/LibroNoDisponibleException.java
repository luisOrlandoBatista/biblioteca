package com.luiso.biblioteca.exception;

public class LibroNoDisponibleException extends ConflictoNegocioException {

    public LibroNoDisponibleException(String titulo) {
        super("El libro '" + titulo + "' ya está prestado");
    }
}