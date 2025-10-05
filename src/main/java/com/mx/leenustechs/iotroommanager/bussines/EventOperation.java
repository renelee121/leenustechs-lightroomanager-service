package com.mx.leenustechs.iotroommanager.bussines;

import org.springframework.stereotype.Service;

import com.mx.leenustechs.iotroommanager.model.GenericEventObject;


@Service
public interface EventOperation {
    void execute(GenericEventObject event);
}