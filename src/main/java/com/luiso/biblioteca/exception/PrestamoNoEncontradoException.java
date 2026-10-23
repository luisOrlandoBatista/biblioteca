package com.luiso.biblioteca.exception;

public class PrestamoNoEncontradoException extends RuntimeException {

    public PrestamoNoEncontradoException(Long id) {
        super("No existe ningún préstamo con id " + id);
    }
}