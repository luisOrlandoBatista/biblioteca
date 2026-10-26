package com.luiso.biblioteca.valoracion.infraestructura.persistencia;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.luiso.biblioteca.valoracion.aplicacion.puerto.ValoracionesPort;
import com.luiso.biblioteca.valoracion.dominio.Valoracion;

// @Primary: si hay varios beans del mismo tipo, Spring inyecta este
@Primary
@Component
class ValoracionesMemoriaAdapter implements ValoracionesPort {

    private final List<Valoracion> valoraciones = new CopyOnWriteArrayList<>(); // lista segura para hilos
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public Valoracion guardar(Valoracion v) {
        Valoracion guardada = new Valoracion(secuencia.incrementAndGet(), v.libroId(), v.socio(),
                v.puntuacion(), v.comentario(), v.fecha());
        valoraciones.add(guardada);
        return guardada;
    }

    @Override
    public boolean existe(Long libroId, String socio) {
        return valoraciones.stream().anyMatch(v -> v.libroId().equals(libroId) && v.socio().equals(socio));
    }

    @Override
    public List<Valoracion> buscarPorLibro(Long libroId) {
        return valoraciones.stream().filter(v -> v.libroId().equals(libroId)).toList();
    }
}