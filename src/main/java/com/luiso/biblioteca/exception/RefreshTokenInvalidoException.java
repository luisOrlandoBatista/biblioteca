package com.luiso.biblioteca.exception;

import org.springframework.security.core.AuthenticationException;

// Hereda de AuthenticationException (de Spring Security), así que tu manejador
// manejarNoAutenticado del paso 6.3 ya la convierte en un 401, sin código nuevo
public class RefreshTokenInvalidoException extends AuthenticationException {

    public RefreshTokenInvalidoException(String mensaje) {
        super(mensaje);
    }
}