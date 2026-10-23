package com.luiso.biblioteca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.luiso.biblioteca.entity.Libro;

import jakarta.persistence.EntityManager;

@Repository
public class LibroRepository {

    // EntityManager: el objeto central de JPA. Spring lo inyecta como cualquier bean
    private final EntityManager entityManager;

    public LibroRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Libro> findAll() {
        // JPQL: "Libro" es la CLASE y "l.id" su CAMPO, no la tabla "libros" ni su columna
        return entityManager.createQuery("SELECT l FROM Libro l ORDER BY l.id", Libro.class)
                .getResultList();
    }

    public Optional<Libro> findById(Long id) {
        // find(): busca por clave primaria. Devuelve null si no existe
        return Optional.ofNullable(entityManager.find(Libro.class, id));
    }

    public List<Libro> findByAutorContaining(String texto) {
        return entityManager.createQuery(
                        "SELECT l FROM Libro l WHERE LOWER(l.autor) LIKE LOWER(:texto) ORDER BY l.id",
                        Libro.class)
                .setParameter("texto", "%" + texto + "%") // parámetros: igual de seguros que en JDBC
                .getResultList();
    }

    public boolean existsById(Long id) {
        Long total = entityManager.createQuery(
                        "SELECT COUNT(l) FROM Libro l WHERE l.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return total > 0;
    }

    public boolean existsByTituloIgnoreCase(String titulo) {
        Long total = entityManager.createQuery(
                        "SELECT COUNT(l) FROM Libro l WHERE LOWER(l.titulo) = LOWER(:titulo)", Long.class)
                .setParameter("titulo", titulo)
                .getSingleResult();
        return total > 0;
    }

    public Libro save(Libro libro) {
        if (libro.getId() == null) {
            entityManager.persist(libro); // INSERT. Después de esto, libro ya tiene su id
            return libro;
        }
        return entityManager.merge(libro); // UPDATE de un libro que ya existía
    }

    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager::remove); // DELETE, si existe
    }
}