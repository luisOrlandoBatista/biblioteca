package com.luiso.biblioteca.demo;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TareasLentasService {

    // Síncrona: quien la llama espera 2 segundos
    public String tareaSincrona(String nombre) {
        esperar(2000);
        return nombre + " en " + Thread.currentThread();
    }

    // Asíncrona: se ejecuta en OTRO hilo. Quien la llama recibe la promesa al instante
    @Async
    public CompletableFuture<String> tareaAsincrona(String nombre) {
        esperar(2000);
        // completedFuture: crea una promesa ya cumplida con este valor
        return CompletableFuture.completedFuture(nombre + " en " + Thread.currentThread());
    }

    private void esperar(long milisegundos) {
        try {
            Thread.sleep(milisegundos); // simula una operación lenta (una llamada por red, por ejemplo)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}