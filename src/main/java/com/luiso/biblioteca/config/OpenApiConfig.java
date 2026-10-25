package com.luiso.biblioteca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bibliotecaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de la Biblioteca")
                        .version("1.0.0")
                        .description("API REST para gestionar el catálogo de libros de la biblioteca.")
                        .contact(new Contact().name("Luis").email("luis@ejemplo.com")))
                // Declara un esquema de seguridad llamado "bearerAuth": token JWT en la cabecera
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                // Y lo aplica a todos los endpoints
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}