package com.luiso.biblioteca.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import com.luiso.biblioteca.dto.PaginaResponse;

import com.luiso.biblioteca.dto.LibroRequest;
import com.luiso.biblioteca.dto.LibroResponse;
import com.luiso.biblioteca.exception.LibroDuplicadoException;
import com.luiso.biblioteca.exception.LibroNoEncontradoException;
import com.luiso.biblioteca.model.Libro;
import com.luiso.biblioteca.repository.LibroRepository;

@Service
public class LibroService {

    private static final Logger log = LoggerFactory.getLogger(LibroService.class);

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

        public PaginaResponse<LibroResponse> listar(String autor, int page, int size, String sort) {
        // 1. Filtrar: todos, o solo los del autor indicado
        List<Libro> libros = (autor == null)
                ? libroRepository.findAll()
                : libroRepository.findByAutorContaining(autor);

        // 2. Ordenar y convertir a DTO
        List<LibroResponse> ordenados = libros.stream()
                .sorted(crearComparador(sort))
                .map(LibroResponse::desde)
                .toList();

        // 3. Paginar
        long totalElementos = ordenados.size();
        // Math.ceil redondea hacia arriba: 12 libros / 5 por página = 2,4 → 3 páginas
        int totalPaginas = (int) Math.ceil((double) totalElementos / size);

        List<LibroResponse> contenido = ordenados.stream()
                .skip((long) page * size) // salta los elementos de las páginas anteriores
                .limit(size)              // se queda con los de esta página
                .toList();

        log.debug("Página {} de tamaño {} ordenada por '{}': {} de {} libros",
                page, size, sort, contenido.size(), totalElementos);

        return new PaginaResponse<>(contenido, page, size, totalElementos, totalPaginas);
    }

    // Convierte el texto "titulo,desc" en un criterio de ordenación (Comparator)
    private Comparator<Libro> crearComparador(String sort) {
        String[] partes = sort.split(",");   // "titulo,desc" → ["titulo", "desc"]
        String campo = partes[0];
        boolean descendente = partes.length > 1 && partes[1].equals("desc");

        // switch con "->": devuelve directamente un valor según el caso (Java 14+)
        Comparator<Libro> comparador = switch (campo) {
            case "titulo"  -> Comparator.comparing(Libro::titulo, String.CASE_INSENSITIVE_ORDER);
            case "autor"   -> Comparator.comparing(Libro::autor, String.CASE_INSENSITIVE_ORDER);
            case "paginas" -> Comparator.comparingInt(Libro::paginas);
            default        -> Comparator.comparing(Libro::id);
        };

        return descendente ? comparador.reversed() : comparador; // reversed() invierte el orden
    }

    public LibroResponse buscarPorId(Long id) {
        log.debug("Buscando libro con id {}", id);
        return LibroResponse.desde(obtenerLibro(id));
    }

    public LibroResponse crear(LibroRequest request) {
        // Regla de negocio: comprueba el estado actual de los datos → va en el servicio
        if (libroRepository.existsByTituloIgnoreCase(request.titulo())) {
            throw new LibroDuplicadoException(request.titulo());
        }
        Libro nuevo = new Libro(null, request.titulo(), request.autor(), request.paginas());
        Libro guardado = libroRepository.save(nuevo);
        log.info("Libro creado con id {}", guardado.id());
        return LibroResponse.desde(guardado);
    }

    public LibroResponse actualizar(Long id, LibroRequest request) {
        // Regla de negocio: comprueba el estado actual de los datos → va en el servicio
        if (libroRepository.existsByTituloIgnoreCase(request.titulo())) {
            throw new LibroDuplicadoException(request.titulo());
        }
        obtenerLibro(id);
        Libro actualizado = libroRepository.save(
                new Libro(id, request.titulo(), request.autor(), request.paginas()));
        log.info("Libro {} actualizado", id);
        return LibroResponse.desde(actualizado);
    }

    public void eliminar(Long id) {
        obtenerLibro(id);
        libroRepository.deleteById(id);
        log.info("Libro {} eliminado", id);
    }

    public String libroRecomendado() {
        return "Te recomiendo leer: Clean Code";
    }

    private Libro obtenerLibro(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new LibroNoEncontradoException(id)); // ya no sabe nada de HTTP
    }
}