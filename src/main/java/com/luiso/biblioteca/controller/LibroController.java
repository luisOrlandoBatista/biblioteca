package com.luiso.biblioteca.controller;

import java.net.URI;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.luiso.biblioteca.dto.LibroRequest;
import com.luiso.biblioteca.dto.LibroResponse;
import com.luiso.biblioteca.dto.PaginaResponse;
import com.luiso.biblioteca.service.LibroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/libros")
@Tag(name = "Libros", description = "Gestión del catálogo de libros") // agrupa los endpoints en Swagger UI
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @Operation(summary = "Listar libros",
               description = "Devuelve los libros paginados y ordenados. Permite filtrar por autor.")
    @ApiResponse(responseCode = "200", description = "Página de libros")
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación u orden no válidos",
                 content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping
    public PaginaResponse<LibroResponse> listar(
            @Parameter(description = "Texto que debe contener el nombre del autor", example = "martin")
            @RequestParam(required = false) String autor,

            @Parameter(description = "Número de página, empezando en 0")
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "La página no puede ser negativa")
            int page,

            @Parameter(description = "Elementos por página (máximo 50)")
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "El tamaño mínimo es 1")
            @Max(value = 50, message = "El tamaño máximo es 50")
            int size,

            @Parameter(description = "Orden: campo,asc o campo,desc. Campos: id, titulo, autor, paginas",
                       example = "titulo,asc")
            @RequestParam(defaultValue = "id,asc")
            @Pattern(regexp = "(id|titulo|autor|paginas)(,(asc|desc))?",
                     message = "Orden no válido. Usa campo,asc o campo,desc con: id, titulo, autor o paginas")
            String sort) {

        return libroService.listar(autor, page, size, sort);
    }

    @Operation(summary = "Obtener un libro por su id")
    @ApiResponse(responseCode = "200", description = "Libro encontrado")
    @ApiResponse(responseCode = "404", description = "No existe ningún libro con ese id",
                 content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{id}")
    public LibroResponse obtener(
            @Parameter(description = "Id del libro", example = "1")
            @PathVariable @Positive Long id) {
        return libroService.buscarPorId(id);
    }

    @Operation(summary = "Crear un libro")
    @ApiResponse(responseCode = "201", description = "Libro creado. La cabecera Location indica su URL")
    @ApiResponse(responseCode = "400", description = "Datos no válidos",
                 content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe un libro con ese título",
                 content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    public ResponseEntity<LibroResponse> crear(@Valid @RequestBody LibroRequest request) {
        LibroResponse creado = libroService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @Operation(summary = "Modificar un libro")
    @ApiResponse(responseCode = "200", description = "Libro modificado")
    @ApiResponse(responseCode = "400", description = "Datos no válidos",
                 content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No existe ningún libro con ese id",
                 content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PutMapping("/{id}")
    public LibroResponse actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return libroService.actualizar(id, request);
    }

    @Operation(summary = "Eliminar un libro")
    @ApiResponse(responseCode = "204", description = "Libro eliminado")
    @ApiResponse(responseCode = "404", description = "No existe ningún libro con ese id",
                 content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}