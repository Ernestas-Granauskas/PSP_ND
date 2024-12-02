package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final Map<String, GameBoard> boards = new HashMap<>();

    @Autowired
    private GameWebSocketHandler webSocketHandler;

    @GetMapping("/startSession")
    public Map<String, String> startSession() {
        String code = String.format("%04d", new Random().nextInt(10000));
        sessions.put(code, new GameSession(code));
        boards.put(code, new GameBoard());
        GameSession session = sessions.get(code);
        session.join();
        System.out.println("Created new session with code: " + code);
        return Map.of("sessionCode", code);
    }

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

    @PostMapping("/toggle")
    public ResponseEntity<String> toggle(@RequestBody Map<String, String> body) {
        System.out.println("TEST");
        String code = body.get("sessionCode");
        int row = Integer.parseInt(body.get("row"));
        int col = Integer.parseInt(body.get("col"));
        GameBoard board = boards.get(code);

        //System.out.printf("output: %s %d %d%n",code,row,col);

        if(board != null) {
            if(!board.isGenerated()) {
                board.generateBoard(row, col);
            }

            board.click(row, col);
            return getResponseMessage(code, board);
        } else {
            return ResponseEntity.badRequest().body("Board not found.");
        }
    }

    @PostMapping("/flag")
    public ResponseEntity<String> flag(@RequestBody Map<String, String> body) {//FlagRequest request) {
        //request.getRow();
        String code = body.get("sessionCode");
        int row = Integer.parseInt(body.get("row"));
        int col = Integer.parseInt(body.get("col"));
        GameBoard board = boards.get(code);
        if(board != null) {
            board.flag(row, col);

            return getResponseMessage(code, board);
        } else {
            return ResponseEntity.badRequest().body("Board not found.");
        }
    }

    private ResponseEntity<String> getResponseMessage(String code, GameBoard board) {
        try {
            GameBoardResponse response = new GameBoardResponse(code, board.getState(), board.getView());

            String updateMessage = new ObjectMapper().writeValueAsString(response);

            webSocketHandler.broadcast(updateMessage);

            return ResponseEntity.ok("State updated and broadcast.");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body("Error processing JSON: " + e.getMessage());
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
