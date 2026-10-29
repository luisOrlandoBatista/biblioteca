package com.luiso.biblioteca.mensajeria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import com.luiso.biblioteca.event.PrestamoCreadoEvent;

@Component
public class PrestamosKafkaPublisher {

    private static final Logger log = LoggerFactory.getLogger(PrestamosKafkaPublisher.class);

    // KafkaTemplate: el objeto para ENVIAR mensajes. Spring Boot lo crea con la configuración del YAML.
    // <String, PrestamoMensaje>: tipo de la clave y tipo del mensaje
    private final KafkaTemplate<String, PrestamoMensaje> kafkaTemplate;

    public PrestamosKafkaPublisher(KafkaTemplate<String, PrestamoMensaje> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Escucha el evento INTERNO y lo publica hacia FUERA
    @ApplicationModuleListener
    public void alCrearPrestamo(PrestamoCreadoEvent evento) {
        PrestamoMensaje mensaje = new PrestamoMensaje(1, evento.prestamoId(), evento.libroId(),
                evento.tituloLibro(), evento.socio(), evento.fechaPrestamo());

        // send(topic, clave, mensaje). La clave decide la partición:
        // todos los mensajes del mismo libro irán a la misma partición, en orden.
        // join(): espera la confirmación de Kafka. Si falla, lanza la excepción
        kafkaTemplate.send(KafkaConfig.TOPIC_PRESTAMOS, String.valueOf(evento.libroId()), mensaje).join();

        log.info("Publicado en Kafka: préstamo {} del libro {}", evento.prestamoId(), evento.libroId());
    }
}