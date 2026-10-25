package com.luiso.biblioteca.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

import com.luiso.biblioteca.config.JwtProperties;
import com.luiso.biblioteca.entity.RefreshToken;
import com.luiso.biblioteca.entity.Usuario;
import com.luiso.biblioteca.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    // SecureRandom: generador aleatorio CRIPTOGRÁFICO. Nunca uses Random o Math.random()
    // para secretos: sus valores se pueden predecir
    private static final SecureRandom GENERADOR = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties properties;
    private final Clock clock;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtProperties properties, Clock clock) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.properties = properties;
        this.clock = clock;
    }

    // Crea un refresh token, guarda su HASH y devuelve el token en claro.
    // Es la ÚNICA vez que existe el token en claro en el servidor: después solo queda el hash.
    // Sin @Transactional propio: se une a la transacción de quien lo llama (AuthService)
    public String crear(Usuario usuario, String familia) {
        byte[] bytes = new byte[32];               // 256 bits aleatorios
        GENERADOR.nextBytes(bytes);
        // Base64 "URL": sin caracteres conflictivos (+ /) y sin relleno (=)
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        LocalDateTime ahora = LocalDateTime.now(clock);
        refreshTokenRepository.save(new RefreshToken(
                calcularHash(token),
                usuario,
                familia,
                ahora.plus(properties.expiracionRefresh()),
                ahora));

        return token;
    }

    // SHA-256 en hexadecimal (64 caracteres)
    public String calcularHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // Todas las JVM están obligadas a incluir SHA-256, así que esto no debería ocurrir nunca.
            // getInstance lanza una excepción CHECKED, y la convertimos en una no comprobada
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}