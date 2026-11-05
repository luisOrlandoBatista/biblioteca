package com.luiso.biblioteca.openlibrary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.luiso.biblioteca.exception.ServicioExternoNoDisponibleException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.observation.annotation.Observed;

// "Gateway" (pasarela): la ÚNICA clase que habla con Open Library.
// Aquí se concentra toda la protección frente a sus fallos
@Component
public class OpenLibraryGateway {

    private static final Logger log = LoggerFactory.getLogger(OpenLibraryGateway.class);

    private static final String CAMPOS = "title,author_name,first_publish_year,number_of_pages_median";
    private static final int MAXIMO_RESULTADOS = 5;

    private final OpenLibraryClient openLibraryClient;

    public OpenLibraryGateway(OpenLibraryClient openLibraryClient) {
        this.openLibraryClient = openLibraryClient;
    }

    // "openlibrary" es el nombre de la configuración de cada patrón (en el YAML).
    // fallbackMethod: el plan B si la llamada no es posible
    @Observed(name = "biblioteca.openlibrary.busqueda") // mide cuántas llamadas hay, cuánto tardan y cuántas fallan
    @Retry(name = "openlibrary")
    @CircuitBreaker(name = "openlibrary", fallbackMethod = "buscarFallback")
    public OpenLibraryBusquedaResponse buscar(String titulo) {
        log.debug("Llamando a Open Library: '{}'", titulo); // se verá una vez por cada intento
        return openLibraryClient.buscar(titulo, CAMPOS, MAXIMO_RESULTADOS);
    }

    // El fallback debe tener los MISMOS parámetros que el método original, más un Throwable al final.
    // Resilience4j lo llama cuando la llamada falla definitivamente o el circuito está abierto
    private OpenLibraryBusquedaResponse buscarFallback(String titulo, Throwable causa) {
        log.warn("Open Library no disponible para '{}': {}", titulo, causa.getClass().getSimpleName());
        throw new ServicioExternoNoDisponibleException("Open Library", causa);
    }
}