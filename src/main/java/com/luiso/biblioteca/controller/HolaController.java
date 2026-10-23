package com.luiso.biblioteca.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luiso.biblioteca.service.SaludoService;

@RestController 
public class HolaController {

    private final SaludoService saludoService;

    // Inyección por CONSTRUCTOR:
    // al crear el controlador, Spring ve que necesita un SaludoService,
    // busca ese bean en el contenedor y lo pasa aquí automáticamente.
    public HolaController(SaludoService saludoService) {
        this.saludoService = saludoService;
    }

    // GET http://localhost:8080/hola → devuelve texto plano
    @GetMapping("/hola")
    public String hola() {
        return saludoService.saludar("Luis"); // usamos el servicio inyectado
    }

    // Record: clase de datos inmutable (te lo expliqué en el paso 1.1)
    record Libro(String titulo, String autor, int paginas) {}

    // GET http://localhost:8080/libro → Spring convierte el objeto a JSON
    @GetMapping("/libro")
    public Libro libro() {
        return new Libro("Clean Code", "Robert C. Martin", 464);
    }
}
