package com.example.demo;

import lombok.Getter;

import java.security.SecureRandom;

public class GameBoard {
    private final int[][] board = new int[12][12];
    @Getter
    private final char[][] view = new char[12][12];
    @Getter
    private boolean generated = false;
    private final SecureRandom random = new SecureRandom();
    @Getter
    private String state = "playing";

    public GameBoard() {
        for(int i=0; i<12; i++) {
            for(int j=0; j<12; j++) {
                view[i][j] = 'h';
            }
        }
    }

    public void generateBoard(int clickRow, int clickCol){
        for(int i=0; i<20; i++) {
            int row = random.nextInt(12);
            int col = random.nextInt(12);
            while(board[row][col] != 0 ||
                    (row >= clickRow -1 && row <= clickRow + 1
                            && col >= clickCol -1 && col <= clickCol + 1)) {
                row = random.nextInt(12);
                col = random.nextInt(12);
            }
            board[row][col] = -1;
        }
        for(int i=0; i<12; i++) {
            for (int j=0; j<12; j++){
                if(board[i][j] != -1){
                    int mines = 0;
                    if(i-1>=0&&j-1>=0)
                    {
                        if(board[i-1][j-1] == -1){
                            mines++;
                        }
                    }
                    if(i-1>=0)
                    {
                        if(board[i-1][j] == -1){
                            mines++;
                        }
                    }
                    if(i-1>=0&&j+1<12)
                    {
                        if(board[i-1][j+1] == -1){
                            mines++;
                        }
                    }
                    if(j-1>=0)
                    {
                        if(board[i][j-1] == -1){
                            mines++;
                        }
                    }
                    if(j+1<12){
                        if(board[i][j+1] == -1){
                            mines++;
                        }
                    }
                    if(i+1<12&&j-1>=0){
                        if(board[i+1][j-1] == -1){
                            mines++;
                        }
                    }
                    if(i+1<12) {
                        if (board[i + 1][j] == -1) {
                            mines++;
                        }
                    }
                    if (i+1<12&&j+1<12) {
                        if (board[i+1][j+1] == -1) {
                            mines++;
                        }
                    }
                    board[i][j] = mines;
                }
            }
        }
        generated = true;
        System.out.println("Board generated");
        for(int i=0; i<12; i++) {
            for(int j=0; j<12; j++){
                if(board[i][j] != -1){
                    System.out.printf(" %d ", board[i][j]);
                }
                else {
                    System.out.printf("%d ", board[i][j]);
                }
            }
            System.out.println();
        }
    }

    public void click(int row, int col) {
        if(view[row][col] == 'h' && state.equals("playing")){
            switch(board[row][col]) {
                case -1:
                    view[row][col] = 'm';
                    state = "fail";
                    break;
                case 0:
                    view[row][col] = '0';
                {
                    if(row-1>=0&&col-1>=0) {
                        click(row-1, col-1);
                    }
                    if(row-1>=0) {
                        click(row-1, col);
                    }
                    if(row-1>=0&&col+1<12) {
                        click(row-1, col+1);
                    }
                    if(col-1>=0) {
                        click(row, col-1);
                    }
                    if(col+1<12) {
                        click(row, col+1);
                    }
                    if(row+1<12&&col-1>=0) {
                        click(row+1, col-1);
                    }
                    if(row+1<12) {
                        click(row+1, col);
                    }
                    if(row+1<12&&col+1<12) {
                        click(row+1, col+1);
                    }
                }
                break;
                case 1:
                    view[row][col] = '1';
                    break;
                case 2:
                    view[row][col] = '2';
                    break;
                case 3:
                    view[row][col] = '3';
                    break;
                case 4:
                    view[row][col] = '4';
                    break;
                case 5:
                    view[row][col] = '5';
                    break;
                case 6:
                    view[row][col] = '6';
                    break;
                case 7:
                    view[row][col] = '7';
                    break;
                case 8:
                    view[row][col] = '8';
                    break;
                default:
                    view[row][col] = 'h';
                    break;
            }
        }
        else if(state.equals("playing") && (view[row][col]=='1'||
                view[row][col]=='2'||
                view[row][col]=='3'||
                view[row][col]=='4'||
                view[row][col]=='5'||
                view[row][col]=='6'||
                view[row][col]=='7'||
                view[row][col]=='8')){
            int flagsAround = 0;
            for(int i=row-1; i<=row+1; i++) {
                for(int j=col-1; j<=col+1; j++) {
                    if(i>=0&&i<12&&j>=0&&j<12) {
                        if(view[i][j] == 'f'){
                            flagsAround++;
                        }
                    }
                }
            }
            if(flagsAround==Character.getNumericValue(view[row][col])) {
                for(int i=row-1; i<=row+1; i++) {
                    for(int j=col-1; j<=col+1; j++) {
                        if(i>=0&&i<12&&j>=0&&j<12) {
                            if(view[i][j] == 'h'){
                                click(i, j);
                            }
                        }
                    }
                }
            }
        }
        boolean checkWin = true;
        for(int i=0; i<12; i++) {
            for(int j=0; j<12; j++) {
                if (((board[i][j] == -1) && (view[i][j] == 'm')) || ((board[i][j] != -1) && (view[i][j] == 'f' || view[i][j] == 'h'))) {
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

    public void flag(int row, int col) {
        if(view[row][col] == 'h' && state.equals("playing")) {
            view[row][col] = 'f';
        }
        else if(view[row][col] == 'f' && state.equals("playing")) {
            view[row][col] = 'h';
        }
    }

}
