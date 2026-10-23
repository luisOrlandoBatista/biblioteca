package com.luiso.biblioteca.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional(readOnly = true)
    public PaginaResponse<LibroResponse> listar(String autor, int page, int size, String sort) {
        // 1. Construir la petición de página: número, tamaño y orden
        Pageable pageable = PageRequest.of(page, size, crearOrden(sort));

        // 2. La base de datos devuelve SOLO las filas de esta página
        Page<Libro> pagina = (autor == null)
                ? libroRepository.findAll(pageable)
                : libroRepository.findByAutorContainingIgnoreCase(autor, pageable);

        log.debug("Página {} de tamaño {} ordenada por '{}': {} de {} libros",
                page, size, sort, pagina.getNumberOfElements(), pagina.getTotalElements());

        // 3. Convertir Page<Libro> en nuestro DTO. La API responde igual que antes
        return new PaginaResponse<>(
                pagina.map(LibroResponse::desde).getContent(), // map: convierte cada Libro en LibroResponse
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages());
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
        libro.actualizar(request.titulo(), request.autor(), request.paginas()); // dirty checking
        log.info("Libro {} actualizado", id);
        return LibroResponse.desde(libro);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new LibroNoEncontradoException(id);
        }
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

    // Convierte "titulo,desc" en un Sort de Spring Data.
    // Sustituye al Comparator: ahora ordena la base de datos, no Java.
    private Sort crearOrden(String sort) {
        String[] partes = sort.split(",");
        String campo = partes[0];
        Sort.Direction direccion = (partes.length > 1 && partes[1].equals("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort.Order orden = new Sort.Order(direccion, campo);

        // ignoreCase() ordena sin distinguir mayúsculas → ORDER BY LOWER(titulo).
        // Solo para textos: LOWER() sobre un número daría error en PostgreSQL.
        if (campo.equals("titulo") || campo.equals("autor")) {
            orden = orden.ignoreCase();
        }
        return Sort.by(orden);
    }
}