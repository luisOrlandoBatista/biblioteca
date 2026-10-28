package com.luiso.biblioteca.openlibrary;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.luiso.biblioteca.exception.ServicioExternoNoDisponibleException;

@Service
public class CatalogoExternoService {

    private static final Logger log = LoggerFactory.getLogger(CatalogoExternoService.class);

    // Pedimos solo los campos que usamos: la respuesta es más pequeña y rápida (paso 4.7, aplicado a una API) number_of_pages_median
    private static final String CAMPOS = "title,author_name,first_publish_year";
    private static final int MAXIMO_RESULTADOS = 5;

    private final OpenLibraryClient openLibraryClient; // se inyecta como cualquier bean

    public CatalogoExternoService(OpenLibraryClient openLibraryClient) {
        this.openLibraryClient = openLibraryClient;
    }

    // @PreAuthorize("hasRole('ADMIN')") // solo quien gestiona el catálogo (paso 6.5)
    @Cacheable(cacheNames = "openlibrary", key = "#p0.trim().toLowerCase()")
    public List<LibroExternoResponse> buscar(String titulo) {
        long inicio = System.currentTimeMillis();
        try {
            OpenLibraryBusquedaResponse respuesta =
                    openLibraryClient.buscar(titulo, CAMPOS, MAXIMO_RESULTADOS);

            log.debug("Open Library: '{}' → {} resultados en {} ms",
                    titulo, respuesta.numFound(), System.currentTimeMillis() - inicio);

            return respuesta.docs().stream()
                    .map(doc -> new LibroExternoResponse(
                            doc.title(),
                            primerAutor(doc.autores()),
                            doc.paginas(),
                            doc.anioPrimeraEdicion()))
                    .toList();

        } catch (RestClientException e) {
            // RestClientException: la excepción base de TODOS los errores de RestClient
            // (no conecta, tiempo agotado, respuesta 4xx/5xx...)
            throw new ServicioExternoNoDisponibleException("Open Library", e);
        }
    }

    private String primerAutor(List<String> autores) {
        return (autores == null || autores.isEmpty()) ? null : autores.getFirst();
    }
}