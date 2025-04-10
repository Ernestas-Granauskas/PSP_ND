package com.example.demo;

import lombok.Getter;

import java.security.SecureRandom;

public class GameBoard {
    private final GameTile[][] board = new GameTile[12][12];
    //private final int[][] board = new int[12][12];
    //@Getter
    //private final char[][] view = new char[12][12];
    @Getter
    private boolean generated = false;
    private final SecureRandom random = new SecureRandom();
    @Getter
    private String state = "playing";

    GameBoard(){
        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 12; j++){
                board[i][j] = new GameTile(0,'h');
            }
        }
    }
//
    public void clearBoard()
    {
        generated = false;
        state = "playing";
        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 12; j++){
                board[i][j] = new GameTile(0,'h');
            }
        }
    }

    public void generateBoard(int clickRow, int clickCol){
        for(int i=0; i<20; i++) {
            int row = random.nextInt(12);
            int col = random.nextInt(12);
            while(board[row][col].getReal() != 0 ||
                    (row >= clickRow -1 && row <= clickRow + 1
                            && col >= clickCol -1 && col <= clickCol + 1)) {
                row = random.nextInt(12);
                col = random.nextInt(12);
            }
            board[row][col].setReal(-1);
        }
        for(int i=0; i<12; i++) {
            for (int j=0; j<12; j++){
                if(board[i][j].getReal() != -1){
                    int mines = 0;
                    for(int a = i-1; a<= i+1; a++){
                        for(int b = j-1; b<= j+1; b++){
                            if(a>=0&&a<12&&b>=0&&b<12){
                                if(board[a][b].getReal() == -1){
                                    mines++;
                                }
                            }
                        }
                    }
                    board[i][j].setReal(mines);
                }
            }
        }
        generated = true;
    }

    private void checkWin() {
        boolean checkWin = true;
        for(int i=0; i<12; i++) {
            for(int j=0; j<12; j++) {
                if (((board[i][j].getReal() == -1) && (board[i][j].getView() == 'm')) || ((board[i][j].getReal() != -1) && (board[i][j].getView() == 'f' || board[i][j].getView() == 'h'))) {
                    checkWin = false;
                    break;
                }
            }
            if(!checkWin) {
                break;
            }
        }
        if(checkWin) {
            state = "win";
        }
    }

    private void clickHidden(int row, int col) {
        switch(board[row][col].getReal()) {
            case -1:
                board[row][col].setView('m');
                state = "fail";
                break;
            case 0:
                board[row][col].setView('0');
                clickNumber(row, col);
            break;
            default:
                board[row][col].setView((char)(board[row][col].getReal()+((int)'0')));
                break;
        }
    }

    private void clickNumber(int row, int col) {
        int flagsAround = 0;
        for(int i=row-1; i<=row+1; i++) {
            for(int j=col-1; j<=col+1; j++) {
                if(i>=0&&i<12&&j>=0&&j<12) {
                    if(board[i][j].getView() == 'f'){
                        flagsAround++;
                    }
                }
            }
        }
        if(flagsAround==Character.getNumericValue(board[row][col].getView())) {
            for(int i=row-1; i<=row+1; i++) {
                for(int j=col-1; j<=col+1; j++) {
                    if(i>=0&&i<12&&j>=0&&j<12) {
                        if(board[i][j].getView() == 'h'){
                            click(i, j);
                        }
                    }
                }
            }
        }
    }

    public void click(int row, int col) {
        if(board[row][col].getView() == 'h' && state.equals("playing")){
            clickHidden(row, col);
        }
        else if(state.equals("playing") && (board[row][col].getView()!='0'||
                board[row][col].getView()!='m'||
                board[row][col].getView()!='f'||
                board[row][col].getView()!='h')){
            clickNumber(row, col);
        }
        checkWin();
    }

    public void flag(int row, int col) {
        board[row][col].flag();
    }

    public char[][] getView(){
        char[][] view = new char[12][12];
        for(int i=0; i<12; i++) {
            for(int j=0; j<12; j++) {
                view[i][j] = board[i][j].getView();
            }
        }
        return view;
    }

}
