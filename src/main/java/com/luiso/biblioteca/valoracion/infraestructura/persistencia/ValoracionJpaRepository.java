package com.luiso.biblioteca.valoracion.infraestructura.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface ValoracionJpaRepository extends JpaRepository<ValoracionEntity, Long> {

    boolean existsByLibroIdAndSocio(Long libroId, String socio);

    List<ValoracionEntity> findByLibroIdOrderByFechaDesc(Long libroId);
}