package com.luiso.biblioteca.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity                    // esta clase representa una tabla de la base de datos
@Table(name = "libros")    // nombre de la tabla. Sin esto, JPA buscaría una tabla "libro"
public class Libro {

    @Id                                                   // clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // el id lo genera PostgreSQL (IDENTITY de schema.sql)
    private Long id;

    // @Column describe la columna. Es opcional: sin ella, JPA usa el nombre del campo
    @Column(nullable = false, unique = true, length = 200)
    private String titulo;

    @Column(nullable = false, length = 100)
    private String autor;

    @Column(nullable = false)
    private int paginas;

    @Column(name = "anio_publicacion")
    private Integer anioPublicacion; // Integer (no int), porque puede ser null

    // Un libro → muchos préstamos.
    // mappedBy = "libro": la relación ya está definida por el campo "libro" de Prestamo,
    // que es el lado propietario (tiene la clave foránea). Este lado es solo "de lectura".
    // @OneToMany ya es LAZY por defecto: los préstamos solo se cargan si se piden.
    @OneToMany(mappedBy = "libro")
    private List<Prestamo> prestamos = new ArrayList<>();

    // Constructor vacío OBLIGATORIO para JPA: Hibernate lo usa para crear el objeto
    // y después rellena los campos con los datos de la fila.
    // protected: JPA puede usarlo, pero tu código no puede crear un Libro vacío por error.
    protected Libro() {
    }

    // Constructor para tu código: crear un libro nuevo (sin id, lo asigna la base de datos)
    public Libro(String titulo, String autor, int paginas) {
        this.titulo = titulo;
        this.autor = autor;
        this.paginas = paginas;
    }

    // En lugar de setters sueltos (setTitulo, setAutor...), un método con significado de negocio.
    // Así es imposible modificar el libro "a medias", y el id no se puede cambiar nunca.
    public void actualizar(String titulo, String autor, int paginas) {
        this.titulo = titulo;
        this.autor = autor;
        this.paginas = paginas;
    }

    // Getters: las entidades no son records, así que usan el estilo clásico getX()
    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getPaginas() {
        return paginas;
    }

    // unmodifiableList: devuelve la lista en modo "solo lectura".
    // Así nadie puede añadir o quitar préstamos desde fuera de forma descontrolada.
    public List<Prestamo> getPrestamos() {
        return Collections.unmodifiableList(prestamos);
    }

    public Integer getAnioPublicacion() {
        return anioPublicacion;
    }
}