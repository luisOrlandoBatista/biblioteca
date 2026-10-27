package com.luiso.biblioteca.openlibrary;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/catalogo-externo")
@Tag(name = "Catálogo externo", description = "Búsqueda de libros en Open Library")
public class CatalogoExternoController {

    private final CatalogoExternoService catalogoExternoService;

    public CatalogoExternoController(CatalogoExternoService catalogoExternoService) {
        this.catalogoExternoService = catalogoExternoService;
    }

    @Operation(summary = "Buscar libros por título en Open Library (solo ADMIN)")
    @GetMapping
    public List<LibroExternoResponse> buscar(
            @RequestParam
            @NotBlank(message = "El título es obligatorio")
            @Size(min = 2, message = "Escribe al menos 2 caracteres")
            String titulo) {
        return catalogoExternoService.buscar(titulo);
    }
}