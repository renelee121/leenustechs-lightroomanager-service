package com.mx.leenustechs.iotroommanager.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mx.leenustechs.iotroommanager.bussines.utils.GenericEventObjectDeserializer;
import com.mx.leenustechs.iotroommanager.bussines.utils.GenericEventObjectSerializer;
import com.mx.leenustechs.iotroommanager.model.GenericEventObject;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import tools.jackson.databind.node.ObjectNode;

@Service
public class MQTTService {

    private final IMqttClient mqttClient;
    private final GenericEventObjectSerializer serializer = new GenericEventObjectSerializer();
    private final GenericEventObjectDeserializer deserializer = new GenericEventObjectDeserializer();
    private final Map<String, CompletableFuture<GenericEventObject>> pendingRequests = new ConcurrentHashMap<>();
    private KafkaPublisherService producerService;

    @Value("${mqtt.topic.request}")
    private String requestTopic;

    @Value("${mqtt.topic.response}")
    private String responseTopic;

    public MQTTService(KafkaPublisherService producerService, IMqttClient mqttClient) throws Exception {
        this.mqttClient = mqttClient;
        this.producerService = producerService;
    }

    /**
     * Publica un GenericEventObject al ESP y espera la respuesta con Mono + retry/backoff
     */
    public void sendCommandAndProcess(GenericEventObject requestObj) {
        String txId = requestObj.getTransactionId().toString();
        String dynamicResponseTopic = responseTopic + "/" + txId;

        Mono.fromFuture(() -> {
            if (!mqttClient.isConnected()) {
                CompletableFuture<GenericEventObject> failedFuture = new CompletableFuture<>();
                failedFuture.completeExceptionally(new IllegalStateException("MQTT no conectado"));
                return failedFuture;
            }

            CompletableFuture<GenericEventObject> responseFuture = new CompletableFuture<>();
            pendingRequests.put(txId, responseFuture);

            try {
                // Suscribirse al tópico dinámico
                mqttClient.subscribe(dynamicResponseTopic, (topic, message) -> {
                    try {
                        GenericEventObject responseObj = deserializer.deserialize(topic, message.getPayload());
                        responseFuture.complete(responseObj);
                        // Desuscribirse tras recibir la respuesta
                        mqttClient.unsubscribe(dynamicResponseTopic);
                    } catch (Exception e) {
                        responseFuture.completeExceptionally(e);
                        mqttClient.unsubscribe(dynamicResponseTopic);
                    }
                });

                // Publicar la petición
                byte[] payload = serializer.serialize(requestTopic, requestObj);
                MqttMessage message = new MqttMessage(payload);
                message.setQos(1);
                mqttClient.publish(requestTopic, message);
                System.out.println("Enviado comando MQTT → TxID [" + txId + "]");

            } catch (Exception e) {
                responseFuture.completeExceptionally(e);
            }

            return responseFuture;
        })
        .timeout(Duration.ofSeconds(5))
        .retryWhen(
            Retry.backoff(3, Duration.ofMillis(1500)) // 3 reintentos: 5s + (3 + 4.5s backoff) + 5s final ≈ 15s total
                .maxBackoff(Duration.ofSeconds(4))
                .filter(ex -> !(ex instanceof InterruptedException))
        )
        .onErrorResume(ex -> Mono.just(buildTimeoutResponse(requestObj)))
        .subscribe(response -> {
            producerService.send(txId, response);
            System.out.println("Respuesta final enviada al producer → TxID [" + txId + "]");
        });
    }


    /**
     * Construye un GenericEventObject con estado TIMEOUT si no hay respuesta
     */
    private GenericEventObject buildTimeoutResponse(GenericEventObject original) {
        try {
            GenericEventObject clone = new GenericEventObject();
            clone.setOperationType(original.getOperationType());
            clone.setTransactionId(original.getTransactionId());

            ObjectNode payload = (ObjectNode) original.getPayload();
            payload.put("response", "TIMEOUT");
            clone.setPayload(payload);

            return clone;
        } catch (Exception e) {
            throw new RuntimeException("Error creando respuesta TIMEOUT", e);
        }
    }
}
