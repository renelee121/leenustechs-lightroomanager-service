package com.mx.leenustechs.iotroommanager.dto;

import java.util.UUID;

import com.mx.leenustechs.iotroommanager.model.GenericEventObject;
import com.mx.leenustechs.iotroommanager.model.request.GenericEventObjectRequest;
import com.mx.leenustechs.iotroommanager.model.response.GenericEventObjectResponse;
import com.mx.leenustechs.iotroommanager.model.type.OperationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericEventObjectDto {
    private UUID transactionId;
    private OperationType operationType;
    private JsonNode payload;

    public GenericEventObjectDto(GenericEventObject event){
        this.transactionId = event.getTransactionId();
        this.operationType = event.getOperationType();
        this.payload = event.getPayload();
    }

    public GenericEventObject toModel(){
        return new GenericEventObject(
            this.transactionId,
            this.operationType,
            this.payload
        );
    }

    public GenericEventObjectDto(GenericEventObjectRequest event){
        this.transactionId = event.getTransactionId();
        this.operationType = event.getOperationType();
        this.payload = event.getPayload();
    }

    public GenericEventObjectRequest toRequest(){
        return new GenericEventObjectRequest(
            this.transactionId,
            this.operationType,
            this.payload
        );
    }

    public GenericEventObjectDto(GenericEventObjectResponse event){
        this.transactionId = event.getTransactionId();
        this.operationType = event.getOperationType();
        this.payload = event.getPayload();
    }

    public GenericEventObjectResponse toResponse(){
        return new GenericEventObjectResponse(
            this.transactionId,
            this.operationType,
            this.payload
        );
    }

}
