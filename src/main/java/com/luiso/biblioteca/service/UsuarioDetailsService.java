package com.luiso.biblioteca.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.entity.Usuario;
import com.luiso.biblioteca.repository.UsuarioRepository;

// Al existir un bean que implementa UserDetailsService, Spring Boot deja de crear
// su usuario en memoria (el de spring.security.user). Otra vez: "tú mandas"
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Spring Security llama a este método en cada intento de autenticación
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // User: la implementación de UserDetails que trae Spring Security.
        // Convertimos NUESTRA entidad al formato que entiende Spring Security
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())   // el hash: Spring lo comparará con el PasswordEncoder
                .roles(usuario.getRol().name())    // "ADMIN" → authority "ROLE_ADMIN"
                .disabled(!usuario.isActivo())     // un usuario desactivado no puede entrar
                .build();
    }
}