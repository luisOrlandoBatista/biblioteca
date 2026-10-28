package com.luiso.biblioteca.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "biblioteca.recordatorios")
public record RecordatoriosProperties(String cron, int diasPrestamo) {}