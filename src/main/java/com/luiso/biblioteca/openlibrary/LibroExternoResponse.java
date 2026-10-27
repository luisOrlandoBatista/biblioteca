package com.luiso.biblioteca.openlibrary;

// NUESTRO formato, independiente del de Open Library.
// Si Open Library cambia su JSON, solo cambia el mapeo del servicio, no nuestra API
public record LibroExternoResponse(String titulo, String autor, Integer paginas, Integer anioPrimeraEdicion) {}