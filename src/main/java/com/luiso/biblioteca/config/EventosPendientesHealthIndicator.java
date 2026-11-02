package com.luiso.biblioteca.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

// Spring Boot detecta los beans que implementan HealthIndicator y los añade a /actuator/health.
// El nombre del componente sale del nombre de la clase sin "HealthIndicator": "eventosPendientes"
@Component
public class EventosPendientesHealthIndicator implements HealthIndicator {

    private static final int UMBRAL = 100; // a partir de cuántos pendientes consideramos que hay un problema

    private final JdbcClient jdbcClient;

    public EventosPendientesHealthIndicator(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Health health() {
        long pendientes = jdbcClient
                .sql("SELECT COUNT(*) FROM event_publication WHERE completion_date IS NULL")
                .query(Long.class)
                .single();

        // Health.up() / Health.down(): el estado. withDetail: información extra (visible para ADMIN)
        Health.Builder estado = (pendientes < UMBRAL) ? Health.up() : Health.down();
        return estado
                .withDetail("pendientes", pendientes)
                .withDetail("umbral", UMBRAL)
                .build();
    }
}