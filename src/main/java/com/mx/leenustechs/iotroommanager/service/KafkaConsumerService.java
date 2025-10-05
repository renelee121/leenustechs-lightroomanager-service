package com.mx.leenustechs.iotroommanager.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.mx.leenustechs.iotroommanager.dto.GenericEventObjectDto;
import com.mx.leenustechs.iotroommanager.model.GenericEventObject;

@Component
public class KafkaConsumerService {

    private OperationTypeService operationTypeService;

    public KafkaConsumerService(OperationTypeService operationTypeService){
        this.operationTypeService = operationTypeService;
    }

    @KafkaListener(topics = "${kafka.request.topic.name}")
    public void listen(GenericEventObject message) {
        GenericEventObjectDto dto = new GenericEventObjectDto(message);
        dto.toModel().getOperationType().execute(dto.toModel(), operationTypeService);
    }
}
