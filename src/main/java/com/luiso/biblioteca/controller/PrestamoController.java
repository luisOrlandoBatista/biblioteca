package com.luiso.biblioteca.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luiso.biblioteca.dto.PrestamoResponse;
import com.luiso.biblioteca.service.PrestamoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
}