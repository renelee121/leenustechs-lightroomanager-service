package com.mx.leenustechs.iotroommanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.mx.leenustechs.iotroommanager.model.GenericEventObject;

@Service
public class KafkaPublisherService {

    @Value("${kafka.response.topic.name}")
    private String topic;

    private final KafkaTemplate<String, GenericEventObject> kafkaTemplate;

    public KafkaPublisherService(KafkaTemplate<String, GenericEventObject> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String key, GenericEventObject event) {
        Message<GenericEventObject>  message = MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .setHeader("kafka_messageKey", key)  // <- en lugar de MESSAGE_KEY
            .setHeader("event-timestamp", System.currentTimeMillis())
            .setHeader("event-type", event.getClass().getSimpleName())
            .build();


        // Usar whenComplete para manejar el resultado del envío
        kafkaTemplate.send(message).whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Mensaje enviado a topic: " + topic);
            } else {
                System.err.println("Error enviando mensaje a topic: " + topic + " - " + ex.getMessage());
            }
        });
    }
}
