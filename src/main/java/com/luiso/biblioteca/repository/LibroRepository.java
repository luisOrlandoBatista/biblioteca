package com.luiso.biblioteca.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.luiso.biblioteca.entity.Libro;

// Ya no es una clase, sino una interfaz. Y ya no lleva @Repository:
// Spring Data detecta las interfaces que heredan de JpaRepository y crea su implementación.
public interface LibroRepository extends JpaRepository<Libro, Long> {

    // → WHERE LOWER(autor) LIKE LOWER('%texto%'), con paginación y ordenación
    Page<Libro> findByAutorContainingIgnoreCase(String autor, Pageable pageable);

    // → SELECT ... WHERE LOWER(titulo) = LOWER(?) (devuelve true o false)
    boolean existsByTituloIgnoreCase(String titulo);
}