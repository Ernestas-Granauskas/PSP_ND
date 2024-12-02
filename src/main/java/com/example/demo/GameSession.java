package com.example.demo;

import lombok.Getter;
import org.springframework.web.bind.annotation.PostMapping;

@Getter
public class GameSession {
    private final String code;
    private int players = 0;
    private static final int MAX_PLAYERS = 2;


    public GameSession(String code) {
        this.code = code;
    }

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

    public boolean isFull() {
        return players >= MAX_PLAYERS;
    }
}
