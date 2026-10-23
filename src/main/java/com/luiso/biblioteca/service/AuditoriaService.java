package com.luiso.biblioteca.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luiso.biblioteca.entity.Auditoria;
import com.luiso.biblioteca.repository.AuditoriaRepository;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final Clock clock; // el bean del paso 2.2

    public AuditoriaService(AuditoriaRepository auditoriaRepository, Clock clock) {
        this.auditoriaRepository = auditoriaRepository;
        this.clock = clock;
    }

    // REQUIRES_NEW: transacción PROPIA e independiente.
    // Se confirma al terminar este método, pase lo que pase después en quien lo llamó.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String accion, String detalle) {
        auditoriaRepository.save(new Auditoria(accion, detalle, LocalDateTime.now(clock)));
    }
}   