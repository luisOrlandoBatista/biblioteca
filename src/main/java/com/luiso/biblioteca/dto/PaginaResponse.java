package com.luiso.biblioteca.dto;

import java.util.List;

// <T> es un "genérico": un tipo que se decide al usarlo.
// PaginaResponse<LibroResponse> será una página de libros,
// y mañana PaginaResponse<AutorResponse> será una página de autores.
// Un solo record sirve para paginar cualquier cosa.
public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamanio,
        long totalElementos,
        int totalPaginas
) {}