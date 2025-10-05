package com.mx.leenustechs.iotroommanager.bussines.utils;

import tools.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.mx.leenustechs.iotroommanager.model.GenericEventObject;

import java.util.Map;

public class GenericEventObjectSerializer implements Serializer<GenericEventObject> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // no-op
    }

    @Override
    public byte[] serialize(String topic, GenericEventObject data) {
        try {
            if (data == null) return null;
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing GenericEventObject", e);
        }
    }

    @Override
    public void close() {
        // no-op
    }
}
