package com.backend.stock.priceengine.controller;

import com.backend.stock.priceengine.dto.GlobalProperties;
import com.backend.stock.priceengine.dto.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


@Controller
@Slf4j
public class RegisterController {
	@Autowired
	private GlobalProperties globalProperties;
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@MessageMapping("/register")
	public void processMessageFromClient(@Payload Message message, SimpMessageHeaderAccessor headerAccessor)
			throws Exception {
		log.info("sessionID" + message.getSessionId());
		globalProperties.addTransactionAndSession(message.getSessionId(), message.getTransactionId());
	}

	@MessageMapping("/unregister")
	public void unregister(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) throws Exception {
		log.info("sessionID" + message.getSessionId());
		globalProperties.removeTransactionAndSession(message.getSessionId());
	}

}
