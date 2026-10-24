package com.luiso.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.luiso.biblioteca.dto.LibroResumen;
import com.luiso.biblioteca.entity.Libro;

// JpaSpecificationExecutor añade métodos que aceptan Specifications,
// por ejemplo findAll(Specification, Pageable)
public interface LibroRepository extends JpaRepository<Libro, Long>, JpaSpecificationExecutor<Libro> {

    boolean existsByTituloIgnoreCase(String titulo);

    List<LibroResumen> findAllByOrderByTituloAsc();
}