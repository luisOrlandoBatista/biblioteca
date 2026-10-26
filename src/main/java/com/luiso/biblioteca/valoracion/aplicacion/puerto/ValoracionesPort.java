package com.luiso.biblioteca.valoracion.aplicacion.puerto;

import java.util.List;

import com.luiso.biblioteca.valoracion.dominio.Valoracion;

// PUERTO DE SALIDA: "necesito guardar y consultar valoraciones".
// Lo DEFINE el negocio, con los métodos que el negocio necesita, y no sabe cómo se implementa
public interface ValoracionesPort {

    Valoracion guardar(Valoracion valoracion);

    boolean existe(Long libroId, String socio);

    List<Valoracion> buscarPorLibro(Long libroId);
}