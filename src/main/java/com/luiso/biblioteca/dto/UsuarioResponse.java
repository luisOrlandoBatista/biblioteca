package com.luiso.biblioteca.dto;

import com.luiso.biblioteca.entity.Usuario;

// ⚠️ NUNCA incluye el password, ni siquiera el hash
public record UsuarioResponse(Long id, String username, String rol) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getRol().name());
    }
}