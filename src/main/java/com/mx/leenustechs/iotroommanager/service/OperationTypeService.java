package com.mx.leenustechs.iotroommanager.service;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mx.leenustechs.iotroommanager.bussines.EventOperation;
import com.mx.leenustechs.iotroommanager.bussines.useCase.*;
import com.mx.leenustechs.iotroommanager.model.type.OperationType;

@Service
public class OperationTypeService {

    private Map<OperationType, EventOperation> operationMap = new EnumMap<>(OperationType.class);

    public OperationTypeService(GPIOEventsUseCase event) {
        operationMap.put(OperationType.INPUT, event);
        operationMap.put(OperationType.OUTPUT, event);
    }

    public EventOperation getOperation(OperationType operationType){
        return operationMap.get(operationType);
    }
}
