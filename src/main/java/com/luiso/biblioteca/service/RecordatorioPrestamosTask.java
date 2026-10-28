package com.luiso.biblioteca.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.config.RecordatoriosProperties;
import com.luiso.biblioteca.entity.Prestamo;
import com.luiso.biblioteca.repository.PrestamoRepository;

@Component
public class RecordatorioPrestamosTask {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioPrestamosTask.class);

    private final PrestamoRepository prestamoRepository;
    private final RecordatoriosProperties properties;
    private final Clock clock;

    public RecordatorioPrestamosTask(PrestamoRepository prestamoRepository,
                                     RecordatoriosProperties properties, Clock clock) {
        this.prestamoRepository = prestamoRepository;
        this.properties = properties;
        this.clock = clock;
    }

    // ${...}: lee la expresión cron de la configuración.
    // zone: en qué zona horaria se interpreta el "09:00"
    @Scheduled(cron = "${biblioteca.recordatorios.cron}", zone = "Europe/Madrid")
    @Transactional(readOnly = true)
    public void recordarPrestamosVencidos() {
        LocalDate limite = LocalDate.now(clock).minusDays(properties.diasPrestamo());
        List<Prestamo> vencidos = prestamoRepository.findByFechaDevolucionIsNullAndFechaPrestamoBefore(limite);

        log.info("Recordatorios: {} préstamos con más de {} días", vencidos.size(), properties.diasPrestamo());
        vencidos.forEach(prestamo -> log.info("[RECORDATORIO] {}: devuelve '{}' (prestado el {})",
                prestamo.getSocio(), prestamo.getLibro().getTitulo(), prestamo.getFechaPrestamo()));
    }
}