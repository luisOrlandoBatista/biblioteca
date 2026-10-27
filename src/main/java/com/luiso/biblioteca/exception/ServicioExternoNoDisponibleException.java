package com.luiso.biblioteca.exception;

public class ServicioExternoNoDisponibleException extends RuntimeException {

    public ServicioExternoNoDisponibleException(String servicio, Throwable causa) {
        super("El servicio externo '" + servicio + "' no está disponible en este momento", causa);
    }
}