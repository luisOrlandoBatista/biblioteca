package com.luiso.biblioteca.mensajeria;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // Constante con el nombre: así productor y consumidor no pueden escribirlo distinto
    public static final String TOPIC_PRESTAMOS = "biblioteca.prestamos";

    // Al arrancar, Spring Kafka crea en el broker todos los topics declarados como bean NewTopic
    // (si ya existen, no hace nada)
    @Bean
    public NewTopic topicPrestamos() {
        return TopicBuilder.name(TOPIC_PRESTAMOS)
                .partitions(3)   // 3 particiones: hasta 3 consumidores del mismo grupo en paralelo
                .replicas(1)     // 1 copia: solo tenemos un nodo. En producción, al menos 3
                .build();
    }
}