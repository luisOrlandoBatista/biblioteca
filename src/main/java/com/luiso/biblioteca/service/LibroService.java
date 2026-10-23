package com.luiso.biblioteca.service;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.dto.LibroRequest;
import com.luiso.biblioteca.dto.LibroResponse;
import com.luiso.biblioteca.dto.PaginaResponse;
import com.luiso.biblioteca.entity.Libro;
import com.luiso.biblioteca.exception.LibroDuplicadoException;
import com.luiso.biblioteca.exception.LibroNoEncontradoException;
import com.luiso.biblioteca.repository.LibroRepository;

@Service
public class LibroService {

    private static final Logger log = LoggerFactory.getLogger(LibroService.class);

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Transactional(readOnly = true) // transacción de solo lectura (más eficiente)
    public PaginaResponse<LibroResponse> listar(String autor, int page, int size, String sort) {
        List<Libro> libros = (autor == null)
                ? libroRepository.findAll()
                : libroRepository.findByAutorContaining(autor);

        List<LibroResponse> ordenados = libros.stream()
                .sorted(crearComparador(sort))
                .map(LibroResponse::desde)
                .toList();

        long totalElementos = ordenados.size();
        int totalPaginas = (int) Math.ceil((double) totalElementos / size);

        List<LibroResponse> contenido = ordenados.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();

        log.debug("Página {} de tamaño {} ordenada por '{}': {} de {} libros",
                page, size, sort, contenido.size(), totalElementos);

        return new PaginaResponse<>(contenido, page, size, totalElementos, totalPaginas);
    }

    @Transactional(readOnly = true)
    public LibroResponse buscarPorId(Long id) {
        log.debug("Buscando libro con id {}", id);
        return LibroResponse.desde(obtenerLibro(id));
    }

    @Transactional
    public LibroResponse crear(LibroRequest request) {
        if (libroRepository.existsByTituloIgnoreCase(request.titulo())) {
            throw new LibroDuplicadoException(request.titulo());
        }
        Libro nuevo = new Libro(request.titulo(), request.autor(), request.paginas());
        Libro guardado = libroRepository.save(nuevo);
        log.info("Libro creado con id {}", guardado.getId());
        return LibroResponse.desde(guardado);
    }

    @Transactional
    public LibroResponse actualizar(Long id, LibroRequest request) {
        Libro libro = obtenerLibro(id);
        // ¡No llamamos a save()! Ver la explicación de "dirty checking" más abajo
        libro.actualizar(request.titulo(), request.autor(), request.paginas());
        log.info("Libro {} actualizado", id);
        return LibroResponse.desde(libro);
    }

    @Transactional
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
                .orElseThrow(() -> new LibroNoEncontradoException(id));
    }

    private Comparator<Libro> crearComparador(String sort) {
        String[] partes = sort.split(",");
        String campo = partes[0];
        boolean descendente = partes.length > 1 && partes[1].equals("desc");

        // Ahora con getters (getTitulo) en lugar de accesores de record (titulo)
        Comparator<Libro> comparador = switch (campo) {
            case "titulo"  -> Comparator.comparing(Libro::getTitulo, String.CASE_INSENSITIVE_ORDER);
            case "autor"   -> Comparator.comparing(Libro::getAutor, String.CASE_INSENSITIVE_ORDER);
            case "paginas" -> Comparator.comparingInt(Libro::getPaginas);
            default        -> Comparator.comparing(Libro::getId);
        };

        return descendente ? comparador.reversed() : comparador;
    }
}