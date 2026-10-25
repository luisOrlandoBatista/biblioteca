package com.luiso.biblioteca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.luiso.biblioteca.entity.Rol;
import com.luiso.biblioteca.entity.Usuario;
import com.luiso.biblioteca.repository.UsuarioRepository;

// @Profile("dev"): esta clase SOLO se carga con el perfil dev (paso 2.5).
// En producción no existe, así que nunca se crearán estos usuarios allí
@Configuration
@Profile("dev")
public class DatosDesarrolloConfig {

    private static final Logger log = LoggerFactory.getLogger(DatosDesarrolloConfig.class);

    @Bean
    public CommandLineRunner crearUsuariosDesarrollo(UsuarioRepository usuarioRepository,
                                                     PasswordEncoder passwordEncoder) {
        return args -> {
            crearSiNoExiste(usuarioRepository, passwordEncoder, "admin", "admin123", Rol.ADMIN);
            crearSiNoExiste(usuarioRepository, passwordEncoder, "ana", "ana12345", Rol.SOCIO);
        };
    }

    private void crearSiNoExiste(UsuarioRepository repositorio, PasswordEncoder encoder,
                                 String username, String password, Rol rol) {
        if (!repositorio.existsByUsername(username)) {
            // encode(): calcula el hash. Es lo único que se guarda
            repositorio.save(new Usuario(username, encoder.encode(password), rol));
            log.info("Usuario de desarrollo creado: {} ({})", username, rol);
        }
    }
}