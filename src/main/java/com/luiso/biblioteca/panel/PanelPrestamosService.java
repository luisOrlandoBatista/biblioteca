package com.luiso.biblioteca.panel;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import com.luiso.biblioteca.event.PrestamoCreadoEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class PanelPrestamosService {

    // multicast: cada elemento se envía a TODOS los suscriptores conectados (todos los paneles abiertos).
    // directBestEffort: si un cliente va lento, se le saltan elementos en lugar de bloquear a los demás;
    // y si no hay nadie conectado, el elemento se descarta (un panel solo muestra lo que pasa mientras está abierto)
    private final Sinks.Many<PrestamoEnVivo> sink = Sinks.many().multicast().directBestEffort();

    // Mundo IMPERATIVO: se ejecuta después del commit de cada préstamo (paso 7.3)
    @TransactionalEventListener
    public void alCrearPrestamo(PrestamoCreadoEvent evento) {
        PrestamoEnVivo dato = new PrestamoEnVivo(
                evento.prestamoId(), evento.tituloLibro(), evento.socio(), evento.fechaPrestamo());

        // Mete el dato en el Sink. Si dos préstamos llegan a la vez desde hilos distintos,
        // busyLooping reintenta durante un máximo de 100 ms en lugar de fallar
        sink.emitNext(dato, Sinks.EmitFailureHandler.busyLooping(Duration.ofMillis(100)));
    }

    // Mundo REACTIVO: un Flux que nunca termina, con cada préstamo según ocurre
    public Flux<PrestamoEnVivo> flujo() {
        return sink.asFlux();
    }
}