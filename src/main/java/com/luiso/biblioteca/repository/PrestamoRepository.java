package com.luiso.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.luiso.biblioteca.entity.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // JOIN FETCH: carga los préstamos Y sus libros en UNA sola consulta con JOIN
    @Query("SELECT p FROM Prestamo p JOIN FETCH p.libro ORDER BY p.id")
    List<Prestamo> findAllConLibro();
}