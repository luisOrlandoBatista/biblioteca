package com.luiso.biblioteca.dto;

import java.util.List;

// Información del usuario que hace la petición
public record UsuarioActualResponse(String username, List<String> authorities) {}