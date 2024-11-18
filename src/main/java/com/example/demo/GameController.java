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
    private final Map<String, GameBoard> boards = new HashMap<>();

    @Autowired
    private GameWebSocketHandler webSocketHandler;

    // Endpoint to start a new game session
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
    public ResponseEntity<String> flag(@RequestBody Map<String, String> body) {
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
        String gameState = board.getState();
        char[][] boardView = board.getView();

        StringBuilder boardStringBuilder = new StringBuilder("[");
        for (int i = 0; i < boardView.length; i++) {
            boardStringBuilder.append("[");
            for (int j = 0; j < boardView[i].length; j++) {
                boardStringBuilder.append("\"").append(boardView[i][j]).append("\"");
                if (j < boardView[i].length - 1) {
                    boardStringBuilder.append(",");  // Add comma between characters
                }
            }
            boardStringBuilder.append("]");
            if (i < boardView.length - 1) {
                boardStringBuilder.append(",");  // Add comma between rows
            }
        }
        boardStringBuilder.append("]");

        String boardString = boardStringBuilder.toString();

        String updateMessage = String.format("{\"sessionCode\":\"%s\", \"state\":\"%s\", \"gameBoard\":%s}", code, gameState, boardString);
        webSocketHandler.broadcast(updateMessage);

        return ResponseEntity.ok("State updated and broadcasted.");
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
