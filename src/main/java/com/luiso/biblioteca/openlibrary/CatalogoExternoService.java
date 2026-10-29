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

        private final OpenLibraryGateway openLibraryGateway;

    public CatalogoExternoService(OpenLibraryGateway openLibraryGateway) {
        this.openLibraryGateway = openLibraryGateway;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(cacheNames = "openlibrary", key = "#p0.trim().toLowerCase()")
    public List<LibroExternoResponse> buscar(String titulo) {
        // Los errores ya los gestiona el gateway (reintentos, circuito y fallback)
        OpenLibraryBusquedaResponse respuesta = openLibraryGateway.buscar(titulo);

        return respuesta.docs().stream()
                .map(doc -> new LibroExternoResponse(
                        doc.title(),
                        primerAutor(doc.autores()),
                        doc.paginas(),
                        doc.anioPrimeraEdicion()))
                .toList();
    }
    
    private String primerAutor(List<String> autores) {
        return (autores == null || autores.isEmpty()) ? null : autores.getFirst();
    }
}