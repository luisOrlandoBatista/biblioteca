package com.luiso.biblioteca.mensajeria;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// Imagina que esto es OTRO sistema (el de estadísticas), en otro proyecto y de otro equipo
@Component
public class EstadisticasPrestamosConsumer {

    private static final Logger log = LoggerFactory.getLogger(EstadisticasPrestamosConsumer.class);

    // Spring Kafka llama a este método por cada mensaje del topic.
    // groupId: el grupo de consumidores. Kafka recuerda por qué offset va este grupo
    @KafkaListener(topics = KafkaConfig.TOPIC_PRESTAMOS, groupId = "estadisticas")
    public void recibir(ConsumerRecord<String, PrestamoMensaje> registro) {
        // ConsumerRecord: el mensaje y sus metadatos (partición, offset, clave...)
        PrestamoMensaje mensaje = registro.value();
        log.info("[ESTADISTICAS] Préstamo {} de '{}' para '{}' | partición {} offset {}",
                mensaje.prestamoId(), mensaje.tituloLibro(), mensaje.socio(),
                registro.partition(), registro.offset());
    }
}