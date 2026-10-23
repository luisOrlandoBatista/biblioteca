package com.luiso.biblioteca.exception;

public class LibroDuplicadoException extends RuntimeException {

    public LibroDuplicadoException(String titulo) {
        super("Ya existe un libro con el título '" + titulo + "'");
    }
}