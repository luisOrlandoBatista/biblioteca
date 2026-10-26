package com.luiso.biblioteca.valoracion.infraestructura.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luiso.biblioteca.valoracion.aplicacion.puerto.ValoracionesUseCase;
import com.luiso.biblioteca.valoracion.aplicacion.puerto.ValorarLibroCommand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

// ADAPTADOR DE ENTRADA: traduce HTTP a una llamada al puerto de entrada.
// Depende de la INTERFAZ ValoracionesUseCase, no de la clase ValoracionesService
@RestController
@RequestMapping("/api/libros/{libroId}/valoraciones")
@Tag(name = "Valoraciones", description = "Valoraciones de los libros por los socios")
public class ValoracionController {

    private final ValoracionesUseCase valoraciones;

    public ValoracionController(ValoracionesUseCase valoraciones) {
        this.valoraciones = valoraciones;
    }

    @Operation(summary = "Valorar un libro (una vez por socio)")
    @PostMapping
    public ResponseEntity<ValoracionResponse> valorar(@PathVariable Long libroId,
                                                      @Valid @RequestBody ValoracionRequest request,
                                                      Authentication authentication) {
        // El socio sale del TOKEN, nunca del cuerpo (paso 6.5)
        ValorarLibroCommand comando = new ValorarLibroCommand(
                libroId, authentication.getName(), request.puntuacion(), request.comentario());
        ValoracionResponse respuesta = ValoracionResponse.desde(valoraciones.valorar(comando));
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Listar las valoraciones de un libro")
    @GetMapping
    public List<ValoracionResponse> listar(@PathVariable Long libroId) {
        return valoraciones.listarPorLibro(libroId).stream()
                .map(ValoracionResponse::desde)
                .toList();
    }
}