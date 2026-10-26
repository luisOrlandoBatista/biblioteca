package com.luiso.biblioteca.valoracion.aplicacion.puerto;

// "Command": los datos para ejecutar un caso de uso.
// Es distinto del DTO web: no sabe nada de JSON ni de validación de Spring
public record ValorarLibroCommand(Long libroId, String socio, int puntuacion, String comentario) {}