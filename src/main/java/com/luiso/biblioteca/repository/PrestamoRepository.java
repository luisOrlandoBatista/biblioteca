package com.luiso.biblioteca.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.luiso.biblioteca.dto.LibroPopularidad;
import com.luiso.biblioteca.entity.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro ORDER BY p.id")
    List<Prestamo> findAllConLibro();

    // ¿Tiene este libro algún préstamo sin devolver?
    // "LibroId" navega por la relación: campo libro → su id
    boolean existsByLibroIdAndFechaDevolucionIsNull(Long libroId);

    // ¿Cuántos préstamos sin devolver tiene este socio?
    long countBySocioAndFechaDevolucionIsNull(String socio);

    // "SELECT new ...": crea un LibroPopularidad por cada fila del resultado.
    // Hay que escribir el nombre COMPLETO de la clase, con su paquete.
    // Pageable: aquí solo lo usamos para limitar cuántos resultados queremos (el "top N").
    @Query("""
            SELECT new com.luiso.biblioteca.dto.LibroPopularidad(l.titulo, COUNT(p))
            FROM Prestamo p JOIN p.libro l
            GROUP BY l.id, l.titulo
            ORDER BY COUNT(p) DESC
            """)
    List<LibroPopularidad> findLibrosMasPrestados(Pageable pageable);

    // Los préstamos de un socio, con su libro en la misma consulta (sin N+1, paso 4.4)
    @EntityGraph(attributePaths = "libro")
    List<Prestamo> findBySocioOrderByIdAsc(String socio);

    // Préstamos sin devolver y prestados ANTES de la fecha límite.
    // @EntityGraph: trae el libro en la misma consulta (sin N+1, paso 4.4)
    @EntityGraph(attributePaths = "libro")
    List<Prestamo> findByFechaDevolucionIsNullAndFechaPrestamoBefore(LocalDate limite);

    long countByFechaDevolucionIsNullAndFechaPrestamoBefore(LocalDate limite);
}