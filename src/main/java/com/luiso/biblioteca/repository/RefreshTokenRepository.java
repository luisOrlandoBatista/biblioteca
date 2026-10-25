package com.luiso.biblioteca.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.luiso.biblioteca.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // @EntityGraph: trae también el usuario en la misma consulta (paso 4.4),
    // porque siempre lo vamos a necesitar para generar el nuevo access token
    @EntityGraph(attributePaths = "usuario")
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // @Modifying: indica que esta @Query MODIFICA datos (UPDATE o DELETE), en lugar de leerlos.
    // Es una actualización "en bloque": un solo UPDATE para todas las filas de la familia,
    // en lugar de cargar cada token y cambiarlo uno por uno.
    // Devuelve cuántas filas se modificaron
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revocado = true WHERE r.familia = :familia AND r.revocado = false")
    int revocarFamilia(@Param("familia") String familia);

    // Para cerrar TODAS las sesiones de un usuario (por ejemplo, al cambiar su contraseña)
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revocado = true WHERE r.usuario.id = :usuarioId AND r.revocado = false")
    int revocarTodosDelUsuario(@Param("usuarioId") Long usuarioId);

    // DELETE en bloque, igual que los @Modifying del paso 6.3b
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiraEn < :limite")
    int borrarCaducados(@Param("limite") LocalDateTime limite);
}