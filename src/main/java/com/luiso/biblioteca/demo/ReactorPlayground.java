package com.luiso.biblioteca.demo;

import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Clase independiente para experimentar. Se ejecuta con su propio main, sin Spring
public class ReactorPlayground {

    public static void main(String[] args) {
        ejemplo1NadaOcurreSinSuscripcion();
        ejemplo2Operadores();
        ejemplo3Mono();
        ejemplo4Errores();
        ejemplo5Asincronia();
        ejemplo6FlatMapFrenteAConcatMap();
        ejemplo7Combinar();
    }

    // 1. Un Flux es una RECETA: no se ejecuta hasta subscribe()
    static void ejemplo1NadaOcurreSinSuscripcion() {
        System.out.println("\n=== 1. Nada ocurre sin suscripción ===");
        Flux<String> libros = Flux.just("Clean Code", "Refactoring", "DDD")
                .doOnNext(libro -> System.out.println("  emitiendo: " + libro)); // doOnNext: "espiar" sin modificar

        System.out.println("Flux creado. ¿Se ha emitido algo? No.");
        libros.subscribe(libro -> System.out.println("  recibido:  " + libro));
    }

    // 2. Operadores: transforman el flujo, igual que los streams de Java
    static void ejemplo2Operadores() {
        System.out.println("\n=== 2. Operadores ===");
        Flux.range(1, 6)                 // 1, 2, 3, 4, 5, 6
                .filter(n -> n % 2 == 0) // 2, 4, 6
                .map(n -> n * n)         // 4, 16, 36
                .subscribe(n -> System.out.println("  " + n));
    }

    // 3. Mono: 0 o 1 elemento
    static void ejemplo3Mono() {
        System.out.println("\n=== 3. Mono ===");
        Mono.just("Effective Java")
                .map(String::toUpperCase)
                .subscribe(titulo -> System.out.println("  Con valor: " + titulo));

        Mono.<String>empty()                                   // un Mono vacío: "no encontrado"
                .defaultIfEmpty("(libro no encontrado)")      // valor por defecto si está vacío
                .subscribe(titulo -> System.out.println("  Vacío: " + titulo));
    }

    // 4. Los errores son señales: terminan el flujo, salvo que los gestiones
    static void ejemplo4Errores() {
        System.out.println("\n=== 4. Errores ===");
        Flux.just(1, 2, 0, 4)
                .map(n -> 10 / n)                              // 10/0 lanza una excepción
                .onErrorResume(error -> {                      // plan B ante un error (como el fallback del 8.5)
                    System.out.println("  error capturado: " + error.getMessage());
                    return Flux.just(-1);                      // sustituye el resto del flujo por -1
                })
                .subscribe(
                        n -> System.out.println("  valor: " + n),             // onNext
                        error -> System.out.println("  ERROR: " + error),     // onError
                        () -> System.out.println("  completado"));            // onComplete
    }

    // 5. Asincronía: los elementos llegan con el tiempo, en otro hilo
    static void ejemplo5Asincronia() {
        System.out.println("\n=== 5. Asincronía ===");
        Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(500)) // cada elemento llega 500 ms después del anterior
                .doOnNext(letra -> System.out.println("  " + letra + " en el hilo " + Thread.currentThread().getName()))
                .blockLast(); // SOLO para esta demo: espera al último elemento. Nunca en código reactivo real
    }

    // 6. flatMap (en paralelo) frente a concatMap (en orden, uno tras otro)
    static void ejemplo6FlatMapFrenteAConcatMap() {
        System.out.println("\n=== 6. flatMap frente a concatMap ===");

        long inicio = System.currentTimeMillis();
        Flux.just(1, 2, 3)
                .concatMap(ReactorPlayground::consultaLenta) // espera a que termine cada una antes de la siguiente
                .blockLast();
        System.out.println("  concatMap: " + (System.currentTimeMillis() - inicio) + " ms");

        inicio = System.currentTimeMillis();
        Flux.just(1, 2, 3)
                .flatMap(ReactorPlayground::consultaLenta)   // lanza las tres a la vez
                .blockLast();
        System.out.println("  flatMap:   " + (System.currentTimeMillis() - inicio) + " ms");
    }

    // Simula una consulta que tarda 1 segundo SIN bloquear ningún hilo
    static Mono<String> consultaLenta(int id) {
        return Mono.just("resultado " + id).delayElement(Duration.ofSeconds(1));
    }

    // 7. Combinar dos fuentes independientes
    static void ejemplo7Combinar() {
        System.out.println("\n=== 7. Combinar con zip ===");
        Mono<String> libro = Mono.just("Clean Code").delayElement(Duration.ofMillis(800));
        Mono<Double> media = Mono.just(4.5).delayElement(Duration.ofMillis(800));

        long inicio = System.currentTimeMillis();
        Mono.zip(libro, media)                                // espera a los DOS, que corren en paralelo
                .map(t -> t.getT1() + " tiene una media de " + t.getT2())
                .doOnNext(texto -> System.out.println("  " + texto))
                .block();
        System.out.println("  tiempo: " + (System.currentTimeMillis() - inicio) + " ms (no 1600)");
    }
}