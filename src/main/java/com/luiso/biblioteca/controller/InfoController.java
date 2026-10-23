package com.luiso.biblioteca.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luiso.biblioteca.config.BibliotecaProperties;

@RestController
public class InfoController {

    private final BibliotecaProperties properties;

    // Las propiedades se inyectan como cualquier otro bean
    public InfoController(BibliotecaProperties properties) {
        this.properties = properties;
    }

    // GET /info → devuelve la configuración de la biblioteca en JSON
    @GetMapping("/info")
    public BibliotecaProperties info() {
        return properties;
    }
}