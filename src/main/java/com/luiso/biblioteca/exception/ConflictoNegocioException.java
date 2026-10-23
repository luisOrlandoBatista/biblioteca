package com.luiso.biblioteca.exception;

// abstract: no se puede usar directamente, solo sirve como base para otras.
// Todas las reglas de negocio que hereden de ella responderán con un 409.
public abstract class ConflictoNegocioException extends RuntimeException {

    protected ConflictoNegocioException(String mensaje) {
        super(mensaje);
    }
}