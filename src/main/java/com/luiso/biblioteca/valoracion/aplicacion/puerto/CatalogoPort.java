package com.luiso.biblioteca.valoracion.aplicacion.puerto;

// PUERTO DE SALIDA: "necesito saber si un libro existe".
// El negocio de valoraciones no conoce la entidad Libro ni su repositorio:
// solo le pregunta al catálogo lo que necesita
public interface CatalogoPort {

    boolean existeLibro(Long libroId);
}