package com.luiso.biblioteca.repository;

import org.springframework.data.jpa.domain.Specification;

import com.luiso.biblioteca.entity.Libro;

// Clase de utilidad: solo métodos static que crean Specifications.
// final + constructor private: no se puede heredar ni crear objetos de ella.
public final class LibroSpecifications {

    private LibroSpecifications() {
    }

    // Cada Specification es una lambda con tres parámetros:
    //   root  → la entidad de la consulta (Libro). root.get("campo") accede a un campo
    //   query → la consulta completa (aquí no la necesitamos)
    //   cb    → CriteriaBuilder: crea las condiciones (like, mayor que, igual...)
    public static Specification<Libro> tituloContiene(String texto) {
        // root.<String>get(...): indicamos que el campo es un String, para poder aplicarle lower()
        return (root, query, cb) ->
                cb.like(cb.lower(root.<String>get("titulo")), "%" + texto.toLowerCase() + "%");
    }

    public static Specification<Libro> autorContiene(String texto) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.<String>get("autor")), "%" + texto.toLowerCase() + "%");
    }

    public static Specification<Libro> paginasMinimas(int minimo) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<Integer>get("paginas"), minimo);
    }

    public static Specification<Libro> paginasMaximas(int maximo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.<Integer>get("paginas"), maximo);
    }
}