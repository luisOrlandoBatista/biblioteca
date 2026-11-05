package com.luiso.biblioteca.metricas;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.luiso.biblioteca.config.RecordatoriosProperties;
import com.luiso.biblioteca.event.PrestamoCreadoEvent;
import com.luiso.biblioteca.repository.PrestamoRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class MetricasPrestamos {

    private final PrestamoRepository prestamoRepository;
    private final RecordatoriosProperties recordatorios;
    private final Clock clock;
    private final Counter prestamosCreados;

    // MeterRegistry: el "registro" de Micrometer donde se dan de alta todas las métricas.
    // Spring Boot lo crea y lo inyecta
    public MetricasPrestamos(MeterRegistry registry, PrestamoRepository prestamoRepository,
                             RecordatoriosProperties recordatorios, Clock clock) {
        this.prestamoRepository = prestamoRepository;
        this.recordatorios = recordatorios;
        this.clock = clock;

        // COUNTER: solo sube. Lo incrementaremos con cada préstamo creado
        this.prestamosCreados = Counter.builder("biblioteca.prestamos.creados")
                .description("Préstamos creados")
                .register(registry);

        // GAUGE: un valor que sube y baja. Micrometer llama a contarVencidos()
        // cada vez que alguien lee las métricas (cada 15 s, cuando pregunte Prometheus)
        Gauge.builder("biblioteca.prestamos.vencidos", this, MetricasPrestamos::contarVencidos)
                .description("Préstamos sin devolver con más días de los permitidos")
                .register(registry);
    }

    // Después del commit: solo cuenta préstamos guardados de verdad (paso 7.3)
    @TransactionalEventListener
    public void alCrearPrestamo(PrestamoCreadoEvent evento) {
        prestamosCreados.increment();
    }

    private double contarVencidos() {
        LocalDate limite = LocalDate.now(clock).minusDays(recordatorios.diasPrestamo());
        return prestamoRepository.countByFechaDevolucionIsNullAndFechaPrestamoBefore(limite);
    }
}