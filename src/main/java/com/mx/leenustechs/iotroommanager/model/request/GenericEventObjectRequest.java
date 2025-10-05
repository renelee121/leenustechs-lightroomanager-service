package com.mx.leenustechs.iotroommanager.model.request;

import java.util.UUID;

import com.mx.leenustechs.iotroommanager.model.type.OperationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericEventObjectRequest{
    private UUID transactionId;
    private OperationType operationType;
    public JsonNode payload;
}
