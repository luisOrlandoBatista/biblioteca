package com.luiso.biblioteca.valoracion.aplicacion;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.exception.LibroNoEncontradoException;
import com.luiso.biblioteca.valoracion.aplicacion.puerto.CatalogoPort;
import com.luiso.biblioteca.valoracion.aplicacion.puerto.ValoracionesPort;
import com.luiso.biblioteca.valoracion.aplicacion.puerto.ValoracionesUseCase;
import com.luiso.biblioteca.valoracion.aplicacion.puerto.ValorarLibroCommand;
import com.luiso.biblioteca.valoracion.dominio.Valoracion;
import com.luiso.biblioteca.valoracion.dominio.ValoracionDuplicadaException;

// Implementa el puerto de entrada usando los puertos de salida.
// Depende SOLO de interfaces (puertos), nunca de JPA ni de controladores
@Service
public class ValoracionesService implements ValoracionesUseCase {

    private final ValoracionesPort valoracionesPort;
    private final CatalogoPort catalogoPort;
    private final Clock clock;

    public ValoracionesService(ValoracionesPort valoracionesPort, CatalogoPort catalogoPort, Clock clock) {
        this.valoracionesPort = valoracionesPort;
        this.catalogoPort = catalogoPort;
        this.clock = clock;
    }

    @Override
    @Transactional
    public Valoracion valorar(ValorarLibroCommand comando) {
        if (!catalogoPort.existeLibro(comando.libroId())) {
            throw new LibroNoEncontradoException(comando.libroId());
        }
        if (valoracionesPort.existe(comando.libroId(), comando.socio())) {
            throw new ValoracionDuplicadaException(comando.libroId(), comando.socio());
        }
        Valoracion nueva = Valoracion.nueva(comando.libroId(), comando.socio(),
                comando.puntuacion(), comando.comentario(), LocalDateTime.now(clock));
        return valoracionesPort.guardar(nueva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Valoracion> listarPorLibro(Long libroId) {
        if (!catalogoPort.existeLibro(libroId)) {
            throw new LibroNoEncontradoException(libroId);
        }
        return valoracionesPort.buscarPorLibro(libroId);
    }
}