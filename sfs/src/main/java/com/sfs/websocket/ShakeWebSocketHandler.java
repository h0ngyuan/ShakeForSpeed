package com.sfs.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfs.kafka.ShakeEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShakeWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionActivityMap = new ConcurrentHashMap<>();
    private final ShakeEventProducer shakeEventProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String activityId = (String) session.getAttributes().get("activityId");
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.put(session.getId(), session);
            if (activityId != null) {
                sessionActivityMap.put(session.getId(), activityId);
            }
            log.info("WebSocket connected: sessionId={}, userId={}, activityId={}", session.getId(), userId, activityId);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String activityId = sessionActivityMap.get(session.getId());
        String userId = (String) session.getAttributes().get("userId");
        
        try {
            JsonNode payload = objectMapper.readTree(message.getPayload());
            String action = payload.has("action") ? payload.get("action").asText() : "";
            
            if ("shake".equals(action)) {
                int count = payload.has("count") ? payload.get("count").asInt() : 1;
                String eventJson = String.format("%s,%s,%d,%d", 
                    activityId, userId, count, System.currentTimeMillis());
                shakeEventProducer.sendShakeEvent(eventJson);
                log.debug("Shake event sent to Kafka: userId={}, activityId={}, count={}", userId, activityId, count);
            }
            
            session.sendMessage(new TextMessage("{\"type\":\"ack\",\"status\":\"ok\"}"));
        } catch (Exception e) {
            log.error("Failed to process WebSocket message: userId={}, activityId={}", userId, activityId, e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        sessionActivityMap.remove(session.getId());
        log.info("WebSocket closed: sessionId={}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error: sessionId={}", session.getId(), exception);
        sessions.remove(session.getId());
        sessionActivityMap.remove(session.getId());
    }

    public void sendToUser(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("Failed to send to user: sessionId={}", sessionId, e);
            }
        }
    }

    public void broadcastToActivity(String activityId, String message) {
        TextMessage textMessage = new TextMessage(message);
        for (Map.Entry<String, String> entry : sessionActivityMap.entrySet()) {
            if (activityId.equals(entry.getValue())) {
                WebSocketSession session = sessions.get(entry.getKey());
                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (Exception e) {
                        log.error("Failed to broadcast to sessionId={}", entry.getKey(), e);
                    }
                }
            }
        }
    }

    public int getOnlineCount(String activityId) {
        return (int) sessionActivityMap.values().stream().filter(a -> a.equals(activityId)).count();
    }
}
