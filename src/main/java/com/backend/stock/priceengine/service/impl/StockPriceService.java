package com.backend.stock.priceengine.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.MessageHeaderInitializer;
import org.springframework.stereotype.Component;

@Component
public class StockPriceService {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private MessageHeaderInitializer headerInitializer;

    public void sendPrice(Object message, String topic, String sessionId){
        //messagingTemplate.convertAndSendToUser(sessionId, topic, message, createHeaders(sessionId));
        messagingTemplate.convertAndSendToUser(sessionId, topic, message, createHeaders(sessionId));
        //messagingTemplate.convertAndSend(topic, message, createHeaders(sessionId));
        return;
    }

    private MessageHeaders createHeaders(String sessionId){
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }


}