package com.example.demo;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class GameController {
    private final Map<String, GameSession> sessions = new HashMap<>();

    @Autowired
    private GameWebSocketHandler webSocketHandler;

    // Endpoint to start a new game session
    @GetMapping("/startSession")
    public Map<String, String> startSession() {
        String code = String.format("%04d", new Random().nextInt(10000));
        sessions.put(code, new GameSession(code));
        GameSession session = sessions.get(code);
        session.join();
        System.out.println("Created new session with code: " + code);
        return Map.of("sessionCode", code);
    }

    // Endpoint to join an existing game session
    @PostMapping("/joinGame")
    public ResponseEntity<Map<String, String>>  joinGame(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        GameSession session = sessions.get(code);

        if (session != null) {
            if(!session.isFull()) {
                session.join();
                return ResponseEntity.ok(Map.of("status", "success", "message", "Successfully joined the session."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "full", "message", "Session is full."));
            }
        } else {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid session code."));
        }
    }

    // Endpoint to toggle game state
    @PostMapping("/toggle")
    public ResponseEntity<String> toggle(@RequestBody Map<String, String> body) {
        String code = body.get("sessionCode");
        GameSession session = sessions.get(code);

        if (session != null) {
            String newState = session.getState();//.equals("0") ? "1" : "0";
            int temp = Integer.parseInt(newState);
            temp++;
            newState = Integer.toString(temp);
            session.setState(newState);

            // Broadcast the updated state to all clients
            String updateMessage = String.format("{\"sessionCode\":\"%s\", \"state\":\"%s\"}", code, newState);
            webSocketHandler.broadcast(updateMessage);

            return ResponseEntity.ok("State updated and broadcasted.");
        } else {
            return ResponseEntity.badRequest().body("Session not found.");
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leaveSession(@RequestBody Map<String,String> body) {
        System.out.println("left");
        GameSession session = sessions.get(body.get("code"));
        if (session != null) {
            session.leave();
        }
        return ResponseEntity.ok().build();
    }
}
