package com.mx.leenustechs.iotroommanager.model.type;

import com.mx.leenustechs.iotroommanager.bussines.EventOperation;
import com.mx.leenustechs.iotroommanager.model.GenericEventObject;
import com.mx.leenustechs.iotroommanager.service.OperationTypeService;

public enum OperationType {
    INPUT,
    OUTPUT;
    
    public void execute(GenericEventObject GenericEventObject, OperationTypeService operationTypeService){
        EventOperation operation = operationTypeService.getOperation(this);
        operation.execute(GenericEventObject);
    }
}
