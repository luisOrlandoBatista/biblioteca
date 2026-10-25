package com.luiso.biblioteca.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.config.JwtProperties;
import com.luiso.biblioteca.dto.LoginRequest;
import com.luiso.biblioteca.dto.TokenResponse;
import com.luiso.biblioteca.entity.RefreshToken;
import com.luiso.biblioteca.entity.Usuario;
import com.luiso.biblioteca.exception.RefreshTokenInvalidoException;
import com.luiso.biblioteca.repository.RefreshTokenRepository;
import com.luiso.biblioteca.repository.UsuarioRepository;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;
    private final JwtProperties properties;
    private final Clock clock;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       RefreshTokenService refreshTokenService,
                       TokenService tokenService,
                       JwtProperties properties,
                       Clock clock) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.tokenService = tokenService;
        this.properties = properties;
        this.clock = clock;
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. Comprobar usuario y contraseña (BD + BCrypt). Si falla: BadCredentialsException → 401
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));

        // 2. Necesitamos la ENTIDAD Usuario para asociarle el refresh token
        Usuario usuario = usuarioRepository.findByUsername(authentication.getName()).orElseThrow();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // 3. Un login = una familia NUEVA de refresh tokens
        String familia = UUID.randomUUID().toString();

        log.info("Login correcto de '{}'", usuario.getUsername());
        return crearRespuesta(usuario.getUsername(), roles, refreshTokenService.crear(usuario, familia));
    }

    // noRollbackFor: ¡CLAVE! (lo explico debajo del código)
    @Transactional(noRollbackFor = RefreshTokenInvalidoException.class)
    public TokenResponse refrescar(String refreshToken) {
        // 1. Buscar el token por su hash
        RefreshToken actual = refreshTokenRepository
                .findByTokenHash(refreshTokenService.calcularHash(refreshToken))
                .orElseThrow(() -> new RefreshTokenInvalidoException("Refresh token desconocido"));

        // 2. ¿Ya estaba revocado? → alguien lo está REUTILIZANDO → posible robo
        if (actual.isRevocado()) {
            int revocados = refreshTokenRepository.revocarFamilia(actual.getFamilia());
            log.warn("Reutilización de refresh token detectada para '{}'. Revocados {} tokens de su familia",
                    actual.getUsuario().getUsername(), revocados);
            throw new RefreshTokenInvalidoException("Refresh token reutilizado");
        }

        // 3. ¿Ha caducado?
        if (actual.estaCaducado(LocalDateTime.now(clock))) {
            throw new RefreshTokenInvalidoException("Refresh token caducado");
        }

        // 4. ¿El usuario sigue activo? (soluciona el problema del mini reto del paso 6.3)
        Usuario usuario = actual.getUsuario();
        if (!usuario.isActivo()) {
            refreshTokenRepository.revocarFamilia(actual.getFamilia());
            throw new RefreshTokenInvalidoException("Usuario desactivado");
        }

        // 5. ROTACIÓN: el token usado se revoca (dirty checking, paso 4.2)
        //    y se crea uno nuevo en la MISMA familia
        actual.revocar();
        String nuevoRefreshToken = refreshTokenService.crear(usuario, actual.getFamilia());

        List<String> roles = List.of("ROLE_" + usuario.getRol().name());
        log.debug("Tokens renovados para '{}'", usuario.getUsername());
        return crearRespuesta(usuario.getUsername(), roles, nuevoRefreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        // Si el token existe, revoca toda su familia (cierra esa sesión en todas sus rotaciones).
        // Si no existe, no hace nada ni da error: así no se revela qué tokens son válidos
        refreshTokenRepository.findByTokenHash(refreshTokenService.calcularHash(refreshToken))
                .ifPresent(token -> {
                    refreshTokenRepository.revocarFamilia(token.getFamilia());
                    log.info("Logout de '{}'", token.getUsuario().getUsername());
                });
    }

    private TokenResponse crearRespuesta(String username, List<String> roles, String refreshToken) {
        String accessToken = tokenService.generarAccessToken(username, roles);
        return new TokenResponse(accessToken, refreshToken, "Bearer", properties.expiracion().toSeconds());
    }
}