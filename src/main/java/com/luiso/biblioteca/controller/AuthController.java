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

import com.luiso.biblioteca.dto.RegistroRequest;
import com.luiso.biblioteca.dto.UsuarioActualResponse;
import com.luiso.biblioteca.dto.UsuarioResponse;
import com.luiso.biblioteca.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro y datos del usuario actual")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Registrar un nuevo socio")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        // 201 sin cabecera Location: no hay un endpoint público para consultar usuarios
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(request));
    }

    // Si un método del controlador recibe un parámetro de tipo Authentication,
    // Spring lo rellena con el usuario autenticado de esta petición (del SecurityContextHolder, paso 6.1)
    @Operation(summary = "Datos del usuario autenticado")
    @GetMapping("/yo")
    public UsuarioActualResponse yo(Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new UsuarioActualResponse(authentication.getName(), authorities);
    }
}