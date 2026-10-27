package com.luiso.biblioteca.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.luiso.biblioteca.event.PrestamoCreadoEvent;

@Component
public class AuditoriaListener {

    private final AuditoriaService auditoriaService;

    public AuditoriaListener(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @TransactionalEventListener
    public void alCrearPrestamo(PrestamoCreadoEvent evento) {
        // Llamada a OTRO bean → pasa por su proxy → su REQUIRES_NEW funciona
        auditoriaService.registrar("PRESTAMO_CREADO",
                "Préstamo " + evento.prestamoId() + ": libro '" + evento.tituloLibro()
                        + "' para '" + evento.socio() + "'");
    }
}