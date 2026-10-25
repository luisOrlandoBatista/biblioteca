package com.luiso.biblioteca.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "biblioteca.cors")
public record CorsProperties(List<String> origenesPermitidos) {}