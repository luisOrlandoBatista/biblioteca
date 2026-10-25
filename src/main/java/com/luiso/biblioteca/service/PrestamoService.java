package com.luiso.biblioteca.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.config.BibliotecaProperties;
import com.luiso.biblioteca.dto.LibroPopularidad;
import com.luiso.biblioteca.dto.PrestamoRequest;
import com.luiso.biblioteca.dto.PrestamoResponse;
import com.luiso.biblioteca.entity.Libro;
import com.luiso.biblioteca.entity.Prestamo;
import com.luiso.biblioteca.exception.LibroNoDisponibleException;
import com.luiso.biblioteca.exception.LibroNoEncontradoException;
import com.luiso.biblioteca.exception.LimitePrestamosExcedidoException;
import com.luiso.biblioteca.exception.PrestamoNoEncontradoException;
import com.luiso.biblioteca.repository.LibroRepository;
import com.luiso.biblioteca.repository.PrestamoRepository;

@Service
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final AuditoriaService auditoriaService;
    private final BibliotecaProperties properties; // max-prestamos, del paso 2.5
    private final Clock clock;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           LibroRepository libroRepository,
                           AuditoriaService auditoriaService,
                           BibliotecaProperties properties,
                           Clock clock) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.auditoriaService = auditoriaService;
        this.properties = properties;
        this.clock = clock;
    }

    @PreAuthorize("hasRole('ADMIN')") // solo ADMIN ve todos los préstamos
    @Transactional(readOnly = true)
    public List<PrestamoResponse> listar() {
        return prestamoRepository.findAllConLibro().stream()
                .map(PrestamoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrestamoResponse buscarPorId(Long id) {
        return prestamoRepository.findById(id)
                .map(PrestamoResponse::desde)
                .orElseThrow(() -> new PrestamoNoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorLibro(Long libroId) {
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new LibroNoEncontradoException(libroId));
        return libro.getPrestamos().stream()
                .map(PrestamoResponse::desde)
                .toList();
    }

    // UNA transacción (REQUIRED) para toda la operación de negocio
    // ADMIN puede crear préstamos para cualquiera.
    // Un socio, solo para sí mismo: el campo "socio" de la petición debe ser su propio usuario
    @PreAuthorize("hasRole('ADMIN') or #request.socio() == authentication.name")
    @Transactional
    public PrestamoResponse crear(PrestamoRequest request) {
        // 1. Auditar el INTENTO. Va en su propia transacción (REQUIRES_NEW):
        //    queda guardado aunque después se rechace el préstamo.
        auditoriaService.registrar("INTENTO_PRESTAMO",
                "El socio '" + request.socio() + "' solicita el libro " + request.libroId());

        // 2. Regla: el libro debe existir (404)
        Libro libro = libroRepository.findById(request.libroId())
                .orElseThrow(() -> new LibroNoEncontradoException(request.libroId()));

        // 3. Regla: el libro no puede estar ya prestado (409)
        if (prestamoRepository.existsByLibroIdAndFechaDevolucionIsNull(libro.getId())) {
            throw new LibroNoDisponibleException(libro.getTitulo());
        }

        // 4. Regla: el socio no puede superar el máximo de préstamos activos (409)
        long activos = prestamoRepository.countBySocioAndFechaDevolucionIsNull(request.socio());
        if (activos >= properties.maxPrestamos()) {
            throw new LimitePrestamosExcedidoException(request.socio(), properties.maxPrestamos());
        }

        // 5. Todo correcto: crear el préstamo con la fecha de hoy (según el Clock)
        Prestamo prestamo = prestamoRepository.save(
                new Prestamo(libro, request.socio(), LocalDate.now(clock)));

        log.info("Préstamo {} creado: libro '{}' para '{}'",
                prestamo.getId(), libro.getTitulo(), request.socio());
        return PrestamoResponse.desde(prestamo);
    }
    
    @Transactional(readOnly = true)
    public List<LibroPopularidad> librosMasPrestados(int top) {
        // Página 0 de tamaño "top" = los "top" primeros resultados
        return prestamoRepository.findLibrosMasPrestados(PageRequest.of(0, top));
    }

    // ADMIN ve los de cualquier socio. Un socio, solo los suyos
    @PreAuthorize("hasRole('ADMIN') or #socio == authentication.name")
    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorSocio(String socio) {
        return prestamoRepository.findBySocioOrderByIdAsc(socio).stream()
                .map(PrestamoResponse::desde)
                .toList();
    }
}