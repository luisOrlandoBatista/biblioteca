package com.luiso.biblioteca.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchos préstamos → un libro.
    // fetch = LAZY: OBLIGATORIO escribirlo, porque en @ManyToOne el defecto es EAGER.
    // optional = false: todo préstamo tiene libro.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "libro_id", nullable = false) // la columna de la clave foránea
    private Libro libro;

    @Column(nullable = false, length = 100)
    private String socio;

    // LocalDate: una fecha sin hora (java.time). Equivale a DATE en la base de datos
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    protected Prestamo() {
    }

    public Prestamo(Libro libro, String socio, LocalDate fechaPrestamo) {
        this.libro = libro;
        this.socio = socio;
        this.fechaPrestamo = fechaPrestamo;
    }

    // Método de negocio: un préstamo está activo si todavía no se ha devuelto
    public boolean estaActivo() {
        return fechaDevolucion == null;
    }

    public Long getId() {
        return id;
    }

    public Libro getLibro() {
        return libro;
    }

    public String getSocio() {
        return socio;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }
}