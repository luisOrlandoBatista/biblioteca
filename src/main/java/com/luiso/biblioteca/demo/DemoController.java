package com.luiso.biblioteca.demo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final ContadorService contadorService;
    private final ObjectProvider<Ticket> ticketProvider; // inyectamos el "pedidor", no el ticket
    private final TareasLentasService tareasLentasService;

    public DemoController(
        ContadorService contadorService,
        ObjectProvider<Ticket> ticketProvider,
        TareasLentasService tareasLentasService
    ) {
        this.contadorService = contadorService;
        this.ticketProvider = ticketProvider;
        this.tareasLentasService = tareasLentasService;
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

    @GetMapping("/demo/secuencial")
    public Map<String, Object> secuencial() {
        long inicio = System.currentTimeMillis();
        // Una detrás de otra: 2 + 2 + 2 segundos
        List<String> resultados = List.of(
                tareasLentasService.tareaSincrona("A"),
                tareasLentasService.tareaSincrona("B"),
                tareasLentasService.tareaSincrona("C"));
        return Map.of("milisegundos", System.currentTimeMillis() - inicio, "resultados", resultados);
    }

    @GetMapping("/demo/paralelo")
    public Map<String, Object> paralelo() {
        long inicio = System.currentTimeMillis();
        // Las tres se lanzan A LA VEZ, cada una en su hilo
        CompletableFuture<String> a = tareasLentasService.tareaAsincrona("A");
        CompletableFuture<String> b = tareasLentasService.tareaAsincrona("B");
        CompletableFuture<String> c = tareasLentasService.tareaAsincrona("C");
        // allOf(...).join(): espera a que terminen las tres
        CompletableFuture.allOf(a, b, c).join();
        List<String> resultados = List.of(a.join(), b.join(), c.join());
        return Map.of("milisegundos", System.currentTimeMillis() - inicio, "resultados", resultados);
    }
}