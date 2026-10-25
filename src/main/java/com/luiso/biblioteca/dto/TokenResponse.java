package com.luiso.biblioteca.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,   // nuevo
        String tokenType,
        long expiresIn         // segundos de vida del ACCESS token
) {}