package com.luiso.biblioteca.valoracion.dominio;

import com.luiso.biblioteca.exception.ConflictoNegocioException;

// Hereda de nuestra excepción base del paso 4.5 → 409 sin código nuevo.
// (Un purista la haría independiente; ConflictoNegocioException es Java puro,
//  sin Spring, así que es una dependencia aceptable)
public class ValoracionDuplicadaException extends ConflictoNegocioException {

    public ValoracionDuplicadaException(Long libroId, String socio) {
        super("El socio '" + socio + "' ya ha valorado el libro " + libroId);
    }
}