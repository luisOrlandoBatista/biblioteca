package com.luiso.biblioteca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.luiso.biblioteca.model.Libro;

@Repository
public class LibroRepository {

    // JdbcClient lo crea Spring Boot automáticamente al detectar el starter JDBC
    // y un DataSource. Se inyecta como cualquier otro bean.
    private final JdbcClient jdbcClient;

    public LibroRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Libro> findAll() {
        return jdbcClient.sql("SELECT id, titulo, autor, paginas FROM libros ORDER BY id")
                // Convierte cada fila en un Libro: la columna "titulo" va al campo titulo, etc.
                .query(Libro.class)
                .list();
    }

    public Optional<Libro> findById(Long id) {
        return jdbcClient.sql("SELECT id, titulo, autor, paginas FROM libros WHERE id = :id")
                .param("id", id)     // rellena :id con el valor, de forma segura
                .query(Libro.class)
                .optional();         // Optional vacío si no hay ninguna fila
    }

    public List<Libro> findByAutorContaining(String texto) {
        return jdbcClient.sql("""
                        SELECT id, titulo, autor, paginas
                        FROM libros
                        WHERE LOWER(autor) LIKE LOWER(:texto)
                        ORDER BY id
                        """)
                .param("texto", "%" + texto + "%") // % en LIKE significa "cualquier texto"
                .query(Libro.class)
                .list();
    }

    public boolean existsById(Long id) {
        return jdbcClient.sql("SELECT EXISTS (SELECT 1 FROM libros WHERE id = :id)")
                .param("id", id)
                .query(Boolean.class)
                .single();           // exactamente un resultado: true o false
    }

    public boolean existsByTituloIgnoreCase(String titulo) {
        return jdbcClient.sql("SELECT EXISTS (SELECT 1 FROM libros WHERE LOWER(titulo) = LOWER(:titulo))")
                .param("titulo", titulo)
                .query(Boolean.class)
                .single();
    }

    // Igual que antes: sin id → INSERT. Con id → UPDATE.
    public Libro save(Libro libro) {
        if (libro.id() == null) {
            return insertar(libro);
        }
        actualizar(libro);
        return libro;
    }

    public void deleteById(Long id) {
        jdbcClient.sql("DELETE FROM libros WHERE id = :id")
                .param("id", id)
                .update();           // update() se usa para INSERT, UPDATE y DELETE
    }

    private Libro insertar(Libro libro) {
        // KeyHolder recoge el id que PostgreSQL genera para la fila nueva
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql("INSERT INTO libros (titulo, autor, paginas) VALUES (:titulo, :autor, :paginas)")
                .param("titulo", libro.titulo())
                .param("autor", libro.autor())
                .param("paginas", libro.paginas())
                .update(keyHolder, "id");   // "id": la columna generada que queremos recuperar

        Long idGenerado = keyHolder.getKeyAs(Long.class);
        return new Libro(idGenerado, libro.titulo(), libro.autor(), libro.paginas());
    }

    private void actualizar(Libro libro) {
        jdbcClient.sql("UPDATE libros SET titulo = :titulo, autor = :autor, paginas = :paginas WHERE id = :id")
                .param("id", libro.id())
                .param("titulo", libro.titulo())
                .param("autor", libro.autor())
                .param("paginas", libro.paginas())
                .update();
    }
}