package com.luiso.biblioteca.exception;

public class UsuarioDuplicadoException extends ConflictoNegocioException {

    public UsuarioDuplicadoException(String username) {
        super("El nombre de usuario '" + username + "' ya está en uso");
    }
}