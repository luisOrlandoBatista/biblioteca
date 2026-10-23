package com.luiso.biblioteca.config;

import java.time.Clock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    @ConditionalOnProperty(name = "biblioteca.bienvenida.activa", havingValue = "true")
    public CommandLineRunner mensajeBienvenida(BibliotecaProperties properties) {
        // Un método @Bean también puede recibir otros beans como parámetros
        return args -> log.info("¡Bienvenido a la API de {}!", properties.nombre());
    }
}