package com.luiso.biblioteca.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.dto.PrestamoResponse;
import com.luiso.biblioteca.entity.Libro;
import com.luiso.biblioteca.exception.LibroNoEncontradoException;
import com.luiso.biblioteca.repository.LibroRepository;
import com.luiso.biblioteca.repository.PrestamoRepository;

@Service
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listar() {
        log.debug("Listando todos los préstamos");
        return prestamoRepository.findAllConLibro().stream()
                .map(PrestamoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorLibro(Long libroId) {
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new LibroNoEncontradoException(libroId));
        // Al acceder a getPrestamos(), Hibernate carga la colección LAZY con una consulta
        return libro.getPrestamos().stream()
                .map(PrestamoResponse::desde)
                .toList();
    }
}