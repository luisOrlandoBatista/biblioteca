package com.luiso.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.luiso.biblioteca.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
}