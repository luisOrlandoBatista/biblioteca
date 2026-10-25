package com.luiso.biblioteca.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password; // el hash, nunca la contraseña original

    // EnumType.STRING: guarda el NOMBRE del enum ("ADMIN") en la columna.
    // ⚠️ El valor por defecto es ORDINAL, que guarda su POSICIÓN (0, 1...).
    // Con ORDINAL, si algún día añades un rol en medio del enum, todos los datos
    // existentes pasan a significar otro rol. Usa SIEMPRE STRING.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(nullable = false)
    private boolean activo = true;

    protected Usuario() {
    }

    public Usuario(String username, String passwordHash, Rol rol) {
        this.username = username;
        this.password = passwordHash;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() { // para boolean, el getter se llama isX en lugar de getX
        return activo;
    }
}