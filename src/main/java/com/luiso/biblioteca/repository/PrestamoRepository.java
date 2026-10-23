package com.luiso.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.luiso.biblioteca.entity.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro ORDER BY p.id")
    List<Prestamo> findAllConLibro();

    // ¿Tiene este libro algún préstamo sin devolver?
    // "LibroId" navega por la relación: campo libro → su id
    boolean existsByLibroIdAndFechaDevolucionIsNull(Long libroId);

    // ¿Cuántos préstamos sin devolver tiene este socio?
    long countBySocioAndFechaDevolucionIsNull(String socio);
}