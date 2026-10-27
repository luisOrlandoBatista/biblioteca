package com.luiso.biblioteca.openlibrary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

// Representa SOLO la parte del JSON de Open Library que nos interesa.
// Los campos del JSON que no estén aquí se ignoran
public record OpenLibraryBusquedaResponse(
        int numFound,
        List<Documento> docs
) {

    // Record anidado: cada libro de la lista "docs"
    public record Documento(
            String title,

            // @JsonProperty: el campo se llama "author_name" en el JSON,
            // pero en Java queremos un nombre en camelCase
            @JsonProperty("author_name") List<String> autores,
            @JsonProperty("first_publish_year") Integer anioPrimeraEdicion,
            @JsonProperty("number_of_pages_median") Integer paginas
    ) {}
}