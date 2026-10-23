package com.luiso.biblioteca.demo;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") // una instancia NUEVA cada vez que se pide a Spring
public class Ticket {

    // UUID: identificador aleatorio único. Cada instancia tendrá uno distinto.
    private final String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }
}