package com.luiso.biblioteca.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class JwtConfig {

    private final SecretKey clave;

    public JwtConfig(JwtProperties properties) {
        // Convierte el texto Base64 en una clave criptográfica para HMAC-SHA256 (HS256)
        byte[] bytes = Base64.getDecoder().decode(properties.secreto());
        this.clave = new SecretKeySpec(bytes, "HmacSHA256");
    }

    // CREA y FIRMA tokens (lo usaremos en el login).
    // Nimbus es la librería que usa Spring Security por debajo para JWT
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(clave));
    }

    // VERIFICA tokens (lo usa Spring Security en cada petición)
    @Bean
    public JwtDecoder jwtDecoder(JwtProperties properties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(clave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        // Además de la firma y la caducidad (que se comprueban siempre),
        // exige que el token lo haya emitido nuestra API ("iss" = biblioteca-api)
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.emisor()));
        return decoder;
    }

    // Convierte el claim "roles" del token en authorities de Spring Security.
    // Por defecto, Spring buscaría un claim "scope" y añadiría el prefijo "SCOPE_".
    // Nosotros guardamos los roles ya completos ("ROLE_ADMIN"), así que sin prefijo
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}