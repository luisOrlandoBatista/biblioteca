package com.luiso.biblioteca.openlibrary;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

// Una interfaz HTTP: describe las peticiones; Spring genera el código que las hace.
// No lleva la URL base (https://openlibrary.org): va en la configuración
public interface OpenLibraryClient {

    // GET /search.json?title=...&fields=...&limit=...
    // Escribimos los nombres en @RequestParam para no depender de -parameters (paso 3.1)
    @GetExchange("/search.json")
    OpenLibraryBusquedaResponse buscar(@RequestParam("title") String titulo,
                                       @RequestParam("fields") String campos,
                                       @RequestParam("limit") int limite);
}