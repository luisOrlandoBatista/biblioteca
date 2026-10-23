package com.luiso.biblioteca.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Spring rellena este record con todas las propiedades que empiezan por "biblioteca."
//   biblioteca.nombre        → nombre
//   biblioteca.max-prestamos → maxPrestamos
@ConfigurationProperties(prefix = "biblioteca")
public record BibliotecaProperties(String nombre, int maxPrestamos) {}