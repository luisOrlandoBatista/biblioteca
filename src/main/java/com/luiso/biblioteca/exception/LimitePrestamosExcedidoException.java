package com.luiso.biblioteca.exception;

public class LimitePrestamosExcedidoException extends ConflictoNegocioException {

    public LimitePrestamosExcedidoException(String socio, int maximo) {
        super("El socio '" + socio + "' ya tiene " + maximo + " préstamos activos, que es el máximo permitido");
    }
}