package com.luiso.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    // Un @Bean de tipo OpenAPI: springdoc lo detecta y usa sus datos
    // como cabecera de la documentación
    @Bean
    public OpenAPI bibliotecaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de la Biblioteca")
                        .version("1.0.0")
                        .description("API REST para gestionar el catálogo de libros de la biblioteca.")
                        .contact(new Contact().name("Luis").email("luis@ejemplo.com")));
    }
}