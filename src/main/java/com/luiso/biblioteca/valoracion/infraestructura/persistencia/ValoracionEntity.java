package com.luiso.biblioteca.valoracion.infraestructura.persistencia;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Entidad JPA: es un detalle de INFRAESTRUCTURA, separado del modelo de dominio.
// Fíjate en libroId: un simple Long, no un @ManyToOne a Libro.
// Así el módulo de valoraciones no depende de la entidad Libro de otro módulo
@Entity
@Table(name = "valoraciones")
public class ValoracionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libro_id", nullable = false)
    private Long libroId;

    @Column(nullable = false, length = 50)
    private String socio;

    @Column(nullable = false)
    private int puntuacion;

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    protected ValoracionEntity() {
    }

    ValoracionEntity(Long libroId, String socio, int puntuacion, String comentario, LocalDateTime fecha) {
        this.libroId = libroId;
        this.socio = socio;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    Long getId() { return id; }
    Long getLibroId() { return libroId; }
    String getSocio() { return socio; }
    int getPuntuacion() { return puntuacion; }
    String getComentario() { return comentario; }
    LocalDateTime getFecha() { return fecha; }
}