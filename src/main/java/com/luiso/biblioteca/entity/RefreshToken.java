package com.luiso.biblioteca.entity;

import java.time.LocalDateTime;

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
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash; // el HASH del token, nunca el token

    // Muchos refresh tokens → un usuario. LAZY, como siempre (paso 4.4)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 36)
    private String familia;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(nullable = false)
    private boolean revocado = false;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    protected RefreshToken() {
    }

    public RefreshToken(String tokenHash, Usuario usuario, String familia,
                        LocalDateTime expiraEn, LocalDateTime creadoEn) {
        this.tokenHash = tokenHash;
        this.usuario = usuario;
        this.familia = familia;
        this.expiraEn = expiraEn;
        this.creadoEn = creadoEn;
    }

    // Métodos de negocio
    public void revocar() {
        this.revocado = true;
    }

    public boolean estaCaducado(LocalDateTime ahora) {
        return ahora.isAfter(expiraEn);
    }

    public boolean isRevocado() {
        return revocado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getFamilia() {
        return familia;
    }
}