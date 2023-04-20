package com.backend.stock.priceengine.dto;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GlobalProperties {
	private Map<String, String> transactionSessionMap = new ConcurrentHashMap<String, String>();
	private Map<String, String> sessionTransactionMap = new ConcurrentHashMap<String, String>();

	public void addTransactionAndSession(String sessionId, String transactionId) {
		this.sessionTransactionMap.put(sessionId, transactionId);
		this.transactionSessionMap.put(transactionId, sessionId);
	}

	public void removeTransactionAndSession(String sessionId) {
		String transactionId = sessionTransactionMap.get(sessionId);
		this.sessionTransactionMap.remove(sessionId);
		this.transactionSessionMap.remove(transactionId);
	}

	public String GetSessionId(String transactionId) {
		return this.transactionSessionMap.get(transactionId);
	}


	public Set<String> getSessionIds() {
		return this.sessionTransactionMap.keySet();
	}
}