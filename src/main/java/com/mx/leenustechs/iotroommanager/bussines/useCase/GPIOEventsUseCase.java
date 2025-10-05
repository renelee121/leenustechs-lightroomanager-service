package com.mx.leenustechs.iotroommanager.bussines.useCase;

import org.springframework.stereotype.Component;

import com.mx.leenustechs.iotroommanager.bussines.EventOperation;
import com.mx.leenustechs.iotroommanager.model.GenericEventObject;
import com.mx.leenustechs.iotroommanager.service.MQTTService;


@Component
public class GPIOEventsUseCase implements EventOperation {
    
    private MQTTService mqttService;

    public GPIOEventsUseCase(MQTTService mqttService){
        
        this.mqttService = mqttService;
    }

    @Override
    public void execute(GenericEventObject event) {
        mqttService.sendCommandAndProcess(event);
    }
}
