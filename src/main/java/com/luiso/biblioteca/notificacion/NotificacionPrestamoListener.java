package com.luiso.biblioteca.notificacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.luiso.biblioteca.event.PrestamoCreadoEvent;

@Component
public class NotificacionPrestamoListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacionPrestamoListener.class);

    // Spring llama a este método cada vez que se publica un PrestamoCreadoEvent
    // (lo sabe por el TIPO del parámetro), y solo DESPUÉS del commit
    @TransactionalEventListener
    public void alCrearPrestamo(PrestamoCreadoEvent evento) {
        // Simulamos el envío de un email. En el Módulo 8 verás cómo hacerlo de verdad
        log.info("[EMAIL] Para: {} | Has tomado prestado '{}' el {}. ¡Disfruta la lectura!",
                evento.socio(), evento.tituloLibro(), evento.fechaPrestamo());
    }
}