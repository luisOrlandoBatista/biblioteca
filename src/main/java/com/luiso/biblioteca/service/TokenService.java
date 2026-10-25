package com.luiso.biblioteca.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.luiso.biblioteca.config.JwtProperties;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;
    private final Clock clock;

    public TokenService(JwtEncoder jwtEncoder, JwtProperties properties, Clock clock) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
        this.clock = clock;
    }

    public String generarAccessToken(String username, List<String> roles) {
        Instant ahora = clock.instant();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.emisor())
                .subject(username)
                .issuedAt(ahora)
                .expiresAt(ahora.plus(properties.expiracion()))
                .claim("roles", roles)
                .build();

        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
    }
}