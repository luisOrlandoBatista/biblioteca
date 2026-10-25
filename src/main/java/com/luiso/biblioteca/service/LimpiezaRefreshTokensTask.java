package com.luiso.biblioteca.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.repository.RefreshTokenRepository;

@Component
public class LimpiezaRefreshTokensTask {

    private static final Logger log = LoggerFactory.getLogger(LimpiezaRefreshTokensTask.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    public LimpiezaRefreshTokensTask(RefreshTokenRepository refreshTokenRepository, Clock clock) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.clock = clock;
    }

    // Expresión cron de Spring: segundo minuto hora día mes día_semana
    // "0 0 3 * * *" = todos los días a las 03:00:00 (hora de poco uso)
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional // @Modifying necesita una transacción
    public void borrarCaducados() {
        int borrados = refreshTokenRepository.borrarCaducados(LocalDateTime.now(clock));
        log.info("Limpieza de refresh tokens: {} filas caducadas borradas", borrados);
    }
}