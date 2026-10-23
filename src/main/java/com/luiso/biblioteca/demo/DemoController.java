package com.luiso.biblioteca.demo;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final ContadorService contadorService;
    private final ObjectProvider<Ticket> ticketProvider; // inyectamos el "pedidor", no el ticket

    public DemoController(ContadorService contadorService, ObjectProvider<Ticket> ticketProvider) {
        this.contadorService = contadorService;
        this.ticketProvider = ticketProvider;
    }

    @GetMapping("/visitas")
    public String visitas() {
        return "Visita número: " + contadorService.registrarVisita();
    }

    @GetMapping("/ticket")
    public String ticket() {
        Ticket ticket = ticketProvider.getObject(); // pide un Ticket NUEVO en cada petición
        return "Ticket: " + ticket.getId();
    }

    @GetMapping("/error-interno")
    public String errorInterno() {
        // Simula un fallo imprevisto, con información interna que el cliente no debe ver
        throw new IllegalStateException("Fallo en la conexión con el servidor interno 10.0.0.5");
    }
}