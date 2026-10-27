package com.luiso.biblioteca.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.dto.LibroRequest;
import com.luiso.biblioteca.dto.LibroResponse;
import com.luiso.biblioteca.dto.LibroResumen;
import com.luiso.biblioteca.dto.PaginaResponse;
import com.luiso.biblioteca.entity.Libro;
import com.luiso.biblioteca.exception.LibroDuplicadoException;
import com.luiso.biblioteca.exception.LibroNoEncontradoException;
import com.luiso.biblioteca.mapper.LibroMapper;
import com.luiso.biblioteca.repository.LibroRepository;
import com.luiso.biblioteca.repository.LibroSpecifications;

@Service
public class LibroService {

    private static final Logger log = LoggerFactory.getLogger(LibroService.class);

    private final LibroRepository libroRepository;
    private final LibroMapper libroMapper;

    public LibroService(LibroRepository libroRepository, LibroMapper libroMapper) {
        this.libroRepository = libroRepository;
        this.libroMapper = libroMapper;
    }

    @Transactional(readOnly = true)
    public PaginaResponse<LibroResponse> listar(String titulo, String autor,
                                                Integer paginasMin, Integer paginasMax,
                                                int page, int size, String sort) {
        // 1. Una Specification por cada filtro que haya llegado en la petición
        List<Specification<Libro>> filtros = new ArrayList<>();
        if (titulo != null) {
            filtros.add(LibroSpecifications.tituloContiene(titulo));
        }
        if (autor != null) {
            filtros.add(LibroSpecifications.autorContiene(autor));
        }
        if (paginasMin != null) {
            filtros.add(LibroSpecifications.paginasMinimas(paginasMin));
        }
        if (paginasMax != null) {
            filtros.add(LibroSpecifications.paginasMaximas(paginasMax));
        }

        // 2. allOf: las combina todas con AND. Si la lista está vacía, no filtra nada
        Specification<Libro> especificacion = Specification.allOf(filtros);

        // 3. Filtro + paginación + orden, en UNA consulta (más el count)
        Pageable pageable = PageRequest.of(page, size, crearOrden(sort));
        Page<Libro> pagina = libroRepository.findAll(especificacion, pageable);

        log.debug("Página {} con {} filtros: {} de {} libros",
                page, filtros.size(), pagina.getNumberOfElements(), pagina.getTotalElements());

        return new PaginaResponse<>(
                pagina.map(libroMapper::aResponse).getContent(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages());
    }

    @Transactional(readOnly = true)
    public LibroResponse buscarPorId(Long id) {
        log.debug("Buscando libro con id {}", id);
        return libroMapper.aResponse(obtenerLibro(id));
    }

    @Transactional
    public LibroResponse crear(LibroRequest request) {
        if (libroRepository.existsByTituloIgnoreCase(request.titulo())) {
            throw new LibroDuplicadoException(request.titulo());
        }
        Libro nuevo = new Libro(request.titulo(), request.autor(), request.paginas());
        Libro guardado = libroRepository.save(nuevo);
        log.info("Libro creado con id {}", guardado.getId());
        return libroMapper.aResponse(guardado);
    }

    @Transactional
    public LibroResponse actualizar(Long id, LibroRequest request) {
        Libro libro = obtenerLibro(id);
        libro.actualizar(request.titulo(), request.autor(), request.paginas()); // dirty checking
        log.info("Libro {} actualizado", id);
        return libroMapper.aResponse(libro);
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

    @Transactional(readOnly = true)
    public List<LibroResumen> listarResumen() {
        return libroRepository.findAllByOrderByTituloAsc();
    }
}