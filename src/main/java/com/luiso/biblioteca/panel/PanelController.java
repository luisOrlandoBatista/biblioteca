package com.luiso.biblioteca.panel;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;

@RestController
@Tag(name = "Panel", description = "Préstamos en tiempo real")
public class PanelController {

    private final PanelPrestamosService panelPrestamosService;

    public PanelController(PanelPrestamosService panelPrestamosService) {
        this.panelPrestamosService = panelPrestamosService;
    }

    // produces = text/event-stream: le dice a Spring que la respuesta es SSE, y no un JSON que termina
    @Operation(summary = "Préstamos en tiempo real (Server-Sent Events). Solo ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/api/panel/prestamos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<PrestamoEnVivo>> prestamosEnVivo() {

        // Cada préstamo, como un evento SSE de tipo "prestamo"
        Flux<ServerSentEvent<PrestamoEnVivo>> prestamos = panelPrestamosService.flujo()
                .map(prestamo -> ServerSentEvent.builder(prestamo).event("prestamo").build());

        // Un "latido" cada 15 segundos: un comentario SSE que el cliente ignora.
        // Sirve para que la conexión no se considere inactiva y no la corten proxies ni balanceadores
        Flux<ServerSentEvent<PrestamoEnVivo>> latidos = Flux.interval(Duration.ofSeconds(15))
                .map(n -> ServerSentEvent.<PrestamoEnVivo>builder().comment("latido").build());

        // merge: une los dos flujos en uno solo, emitiendo lo que llegue de cualquiera
        return Flux.merge(prestamos, latidos);
    }
}