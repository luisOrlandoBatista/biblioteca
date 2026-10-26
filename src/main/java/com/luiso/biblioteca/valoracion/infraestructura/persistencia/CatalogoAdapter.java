package com.luiso.biblioteca.valoracion.infraestructura.persistencia;

import org.springframework.stereotype.Component;

import com.luiso.biblioteca.repository.LibroRepository;
import com.luiso.biblioteca.valoracion.aplicacion.puerto.CatalogoPort;

// Adapta el LibroRepository que ya existía (de la parte "en capas")
// al puerto que necesita el negocio de valoraciones
@Component
class CatalogoAdapter implements CatalogoPort {

    private final LibroRepository libroRepository;

    CatalogoAdapter(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public boolean existeLibro(Long libroId) {
        return libroRepository.existsById(libroId);
    }
}