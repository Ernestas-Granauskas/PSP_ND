package com.example.demo;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.demo.GameController;
import com.example.demo.GameSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.util.Map;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, GameSession> sessions;

    public MyWebSocketHandler(GameController gameController) {
        this.sessions = gameController.getSessions(); // Expose your sessions map via getter
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extract sessionCode from URI path
        String path = session.getUri().getPath();
        String sessionCode = path.substring(path.lastIndexOf('/') + 1);

        GameSession gameSession = sessions.get(sessionCode);
        if (gameSession != null && !gameSession.isFull()) {
            gameSession.join(session);
            System.out.println("Player joined session " + sessionCode + " with WebSocket " + session.getId());
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Session full or not found"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        for (GameSession sessionObj : sessions.values()) {
            if (sessionObj.getSockets().contains(session)) {
                sessionObj.leave(session);
                System.out.println("WebSocket disconnected from session " + sessionObj.getCode());
                break;
            }
        }
    }
}
