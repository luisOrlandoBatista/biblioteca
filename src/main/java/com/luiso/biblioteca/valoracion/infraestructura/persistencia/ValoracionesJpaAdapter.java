package com.luiso.biblioteca.valoracion.infraestructura.persistencia;

import java.util.List;

import org.springframework.stereotype.Component;

import com.luiso.biblioteca.valoracion.aplicacion.puerto.ValoracionesPort;
import com.luiso.biblioteca.valoracion.dominio.Valoracion;

// ADAPTADOR DE SALIDA: implementa el puerto con JPA.
// Traduce entre el modelo de dominio (Valoracion) y la entidad JPA (ValoracionEntity)
@Component
class ValoracionesJpaAdapter implements ValoracionesPort {

    private final ValoracionJpaRepository repository;

    ValoracionesJpaAdapter(ValoracionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Valoracion guardar(Valoracion valoracion) {
        ValoracionEntity entidad = new ValoracionEntity(valoracion.libroId(), valoracion.socio(),
                valoracion.puntuacion(), valoracion.comentario(), valoracion.fecha());
        return aDominio(repository.save(entidad));
    }

    @Override
    public boolean existe(Long libroId, String socio) {
        return repository.existsByLibroIdAndSocio(libroId, socio);
    }

    @Override
    public List<Valoracion> buscarPorLibro(Long libroId) {
        return repository.findByLibroIdOrderByFechaDesc(libroId).stream()
                .map(this::aDominio)
                .toList();
    }

    private Valoracion aDominio(ValoracionEntity e) {
        return new Valoracion(e.getId(), e.getLibroId(), e.getSocio(),
                e.getPuntuacion(), e.getComentario(), e.getFecha());
    }
}