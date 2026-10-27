package com.luiso.biblioteca.openlibrary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

// Registra OpenLibraryClient como bean, dentro de un GRUPO llamado "openlibrary".
// Un grupo es un conjunto de interfaces que comparten configuración (URL base, cabeceras, tiempos...)
@Configuration
@ImportHttpServices(group = "openlibrary", types = OpenLibraryClient.class)
public class OpenLibraryConfig {

    // Personaliza los clientes del grupo "openlibrary": añade una cabecera a TODAS sus peticiones.
    // Open Library pide que las aplicaciones se identifiquen con un User-Agent descriptivo
    @Bean
    public RestClientHttpServiceGroupConfigurer openLibraryCabeceras() {
        return grupos -> grupos.forEachClient((grupo, builder) -> {
            if (grupo.name().equals("openlibrary")) {
                builder.defaultHeader(HttpHeaders.USER_AGENT, "BibliotecaCurso/1.0 (luis@ejemplo.com)");
            }
        });
    }
}