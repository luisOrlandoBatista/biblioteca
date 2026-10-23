package com.luiso.biblioteca.exception;

// Excepción de negocio: "el libro buscado no existe".
// No sabe nada de HTTP: solo describe lo que ha pasado.
// Hereda de RuntimeException (excepción "no comprobada"), así que no hace falta
// declararla con "throws" en cada método. Es la convención en Spring.
public class LibroNoEncontradoException extends RuntimeException {

    public LibroNoEncontradoException(Long id) {
        super("No existe ningún libro con id " + id); // el mensaje del error
    }
}