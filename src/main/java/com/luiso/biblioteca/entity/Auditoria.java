package com.luiso.biblioteca.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(nullable = false, length = 500)
    private String detalle;

    // LocalDateTime: fecha y hora, sin zona horaria. Equivale a TIMESTAMP
    @Column(nullable = false)
    private LocalDateTime fecha;

    protected Auditoria() {
    }

    public Auditoria(String accion, String detalle, LocalDateTime fecha) {
        this.accion = accion;
        this.detalle = detalle;
        this.fecha = fecha;
    }
}