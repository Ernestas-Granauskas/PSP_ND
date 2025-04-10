package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class GameController {
    @Getter
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, GameBoard> boards = new HashMap<>();

    @Autowired
    private GameWebSocketHandler webSocketHandler;

    @GetMapping("/startSession")
    public ResponseEntity<Map<String, String>> startSession() {
        String code = String.format("%04d", new Random().nextInt(10000));
        sessions.put(code, new GameSession(code));
        boards.put(code, new GameBoard());
        GameSession session = sessions.get(code);
        //session.join();
        System.out.println("Created new session with code: " + code);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("sessionCode", code));
    }

    @PostMapping("/joinGame")
    public ResponseEntity<Map<String, String>>  joinGame(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        GameSession session = sessions.get(code);

        if (session != null) {
            if(!session.isFull()) {
                //session.join();
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
            // Retrieve the GameSession from the sessions map
            GameSession gameSession = sessions.get(code);

            // If no session is found, return an error
            if (gameSession == null) {
                return ResponseEntity.status(404).body("Session not found.");
            }

            // Create the response object to be sent
            GameBoardResponse response = new GameBoardResponse(code, board.getState(), board.getView());

            // Convert the response to JSON
            String updateMessage = new ObjectMapper().writeValueAsString(response);

            // Send the message to each WebSocket in the game session's list of sockets
            for (WebSocketSession socket : gameSession.getSockets()) {
                try {
                    if (socket.isOpen()) {
                        socket.sendMessage(new TextMessage(updateMessage)); // Send the message
                    }
                } catch (Exception e) {
                    // Log or handle any errors if necessary
                    System.err.println("Error sending message to WebSocket session: " + e.getMessage());
                }
            }

            return ResponseEntity.ok("State updated and broadcast to connected players.");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body("Error processing JSON: " + e.getMessage());
        }
    }



//    private ResponseEntity<String> getResponseMessage(String code, GameBoard board) {
//        try {
//            GameBoardResponse response = new GameBoardResponse(code, board.getState(), board.getView());
//
//            String updateMessage = new ObjectMapper().writeValueAsString(response);
//
//            webSocketHandler.broadcast(updateMessage);
//
//            return ResponseEntity.ok("State updated and broadcast.");
//        } catch (JsonProcessingException e) {
//            return ResponseEntity.status(500).body("Error processing JSON: " + e.getMessage());
//        }
//    }

//    @PostMapping("/leave")
//    public ResponseEntity<Void> leaveSession(@RequestBody Map<String,String> body) {
//        System.out.println("left");
//        GameSession session = sessions.get(body.get("code"));
//        if (session != null) {
//            session.leave();
//        }
//        return ResponseEntity.ok().build();
//    }



}
