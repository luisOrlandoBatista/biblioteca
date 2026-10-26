package com.luiso.biblioteca.valoracion.aplicacion.puerto;

import java.util.List;

import com.luiso.biblioteca.valoracion.dominio.Valoracion;

// PUERTO DE ENTRADA: los casos de uso que ofrece esta funcionalidad
public interface ValoracionesUseCase {

    Valoracion valorar(ValorarLibroCommand comando);

    List<Valoracion> listarPorLibro(Long libroId);
}