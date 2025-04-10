package com.example.demo;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class GameSession {
    private final String code;
    private int players = 0;
    private static final int MAX_PLAYERS = 2;
    private final List<WebSocketSession> sockets = new CopyOnWriteArrayList<>();

    public GameSession(String code) {
        this.code = code;
    }

    public boolean check_space()
    {
        return players < MAX_PLAYERS;
    }

    public boolean join(WebSocketSession socket) {
        if (players < MAX_PLAYERS) {
            players++;
            sockets.add(socket);
            return true;
        }
        return false;
    }

    public void leave(WebSocketSession socket) {
        players--;
        sockets.remove(socket);
    }

    public boolean isFull() {
        return players >= MAX_PLAYERS;
    }
}
