package com.luiso.biblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luiso.biblioteca.dto.LoginRequest;
import com.luiso.biblioteca.dto.RefreshRequest;
import com.luiso.biblioteca.dto.RegistroRequest;
import com.luiso.biblioteca.dto.TokenResponse;
import com.luiso.biblioteca.dto.UsuarioActualResponse;
import com.luiso.biblioteca.dto.UsuarioResponse;
import com.luiso.biblioteca.service.AuthService;
import com.luiso.biblioteca.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro, login, renovación de tokens y logout")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    public AuthController(UsuarioService usuarioService, AuthService authService) {
        this.usuarioService = usuarioService;
        this.authService = authService;
    }

    @Operation(summary = "Registrar un nuevo socio")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(request));
    }

    @Operation(summary = "Iniciar sesión: devuelve un access token y un refresh token")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Renovar el access token usando el refresh token")
    @PostMapping("/refresh")
    public TokenResponse refrescar(@Valid @RequestBody RefreshRequest request) {
        return authService.refrescar(request.refreshToken());
    }

    @Operation(summary = "Cerrar sesión: revoca el refresh token")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build(); // 204, siempre
    }

    @Operation(summary = "Datos del usuario autenticado")
    @GetMapping("/yo")
    public UsuarioActualResponse yo(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new UsuarioActualResponse(authentication.getName(), authorities);
    }
}