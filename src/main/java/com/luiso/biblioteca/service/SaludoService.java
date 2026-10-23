package com.luiso.biblioteca.service;

import java.time.Clock;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

@Service
public class SaludoService {

    private final Clock clock;

    // Se inyecta igual que cualquier otro bean, aunque venga de un @Bean
    public SaludoService(Clock clock) {
        this.clock = clock;
    }

    public String saludar(String nombre) {
        int hora = LocalTime.now(clock).getHour(); // hora actual según el Clock

        String saludo;
        if (hora < 12) {
            saludo = "Buenos días";
        } else if (hora < 20) {
            saludo = "Buenas tardes";
        } else {
            saludo = "Buenas noches";
        }
        return saludo + " " + nombre + ", te saluda el SaludoService!";
    }
}