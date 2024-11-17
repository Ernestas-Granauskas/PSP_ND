package com.example.demo;

import org.springframework.web.bind.annotation.PostMapping;

public class GameSession {
    private String code;
    private String state = "0";  // Example state, could be improved with an enum
    private int players = 0;
    private static final int MAX_PLAYERS = 2;


    public GameSession(String code) {
        this.code = code;
    }

    // Getter for code (if needed elsewhere in the app)
    public String getCode() {
        return code;
    }

    // Getter for players count (optional, useful for debugging or status checks)
    public int getPlayers() {
        return players;
    }

    // Join the game if there is space (handling player limits)
    public boolean join() {
        if (players < MAX_PLAYERS) {
            players++;
            return true;
        }
        return false; // Could be extended to log "session full" or provide a message
    }

    public void leave() {
        players--;
    }

    // Set the state of the game (could be improved with an enum)
    public void setState(String newState) {
        this.state = newState;
    }

    // Get the current state of the game
    public String getState() {
        return state;
    }

    // You could add a method to check if the session is full
    public boolean isFull() {
        return players >= MAX_PLAYERS;
    }
}
