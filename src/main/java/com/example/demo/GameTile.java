package com.example.demo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameTile {
    private int real;
    private char view;

    GameTile(int real, char view) {
        this.real = real;
        this.view = view;
    }
    public void flag() {
        if(view == 'h') {
            view = 'f';
        }
        else if(view == 'f') {
            view = 'h';
        }
    }

}
