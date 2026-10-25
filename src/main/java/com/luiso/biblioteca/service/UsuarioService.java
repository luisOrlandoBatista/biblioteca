package com.luiso.biblioteca.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.dto.RegistroRequest;
import com.luiso.biblioteca.dto.UsuarioResponse;
import com.luiso.biblioteca.entity.Rol;
import com.luiso.biblioteca.entity.Usuario;
import com.luiso.biblioteca.exception.UsuarioDuplicadoException;
import com.luiso.biblioteca.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new UsuarioDuplicadoException(request.username());
        }
        String hash = passwordEncoder.encode(request.password()); // solo se guarda el hash
        Usuario usuario = usuarioRepository.save(new Usuario(request.username(), hash, Rol.SOCIO));

        // ⚠️ Nunca registres contraseñas en los logs (paso 2.6), ni siquiera el hash
        log.info("Usuario registrado: {}", usuario.getUsername());
        return UsuarioResponse.desde(usuario);
    }
}