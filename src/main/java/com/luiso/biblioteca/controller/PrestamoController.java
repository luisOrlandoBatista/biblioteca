package com.luiso.biblioteca.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.luiso.biblioteca.dto.LibroPopularidad;
import com.luiso.biblioteca.dto.PrestamoRequest;
import com.luiso.biblioteca.dto.PrestamoResponse;
import com.luiso.biblioteca.service.PrestamoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api")
@Tag(name = "Préstamos", description = "Consulta de préstamos de libros")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @Operation(summary = "Listar todos los préstamos")
    @GetMapping("/prestamos")
    public List<PrestamoResponse> listar() {
        return prestamoService.listar();
    }

    // URL anidada: los préstamos "pertenecen" a un libro → /api/libros/{id}/prestamos
    @Operation(summary = "Listar los préstamos de un libro")
    @GetMapping("/libros/{libroId}/prestamos")
    public List<PrestamoResponse> listarPorLibro(@PathVariable Long libroId) {
        return prestamoService.listarPorLibro(libroId);
    }

    @Operation(summary = "Obtener un préstamo por su id")
    @GetMapping("/prestamos/{id}")
    public PrestamoResponse obtener(@PathVariable Long id) {
        return prestamoService.buscarPorId(id);
    }

    @Operation(summary = "Crear un préstamo")
    @PostMapping("/prestamos")
    public ResponseEntity<PrestamoResponse> crear(@Valid @RequestBody PrestamoRequest request) {
        PrestamoResponse creado = prestamoService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @Operation(summary = "Libros más prestados")
    @GetMapping("/prestamos/estadisticas/populares")
    public List<LibroPopularidad> librosMasPrestados(
            @RequestParam(defaultValue = "5") @Min(1) @Max(20) int top) {
        return prestamoService.librosMasPrestados(top);
    }

    @Operation(summary = "Préstamos de un socio (un socio solo puede ver los suyos)")
    @GetMapping("/prestamos/socio/{socio}")
    public List<PrestamoResponse> listarPorSocio(@PathVariable String socio) {
        return prestamoService.listarPorSocio(socio);
    }
}