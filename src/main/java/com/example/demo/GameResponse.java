public class GameResponse {
    private String sessionCode;
    private String state;
    private char[][] gameBoard;

    // Constructor
    public GameResponse(String sessionCode, String state, char[][] gameBoard) {
        this.sessionCode = sessionCode;
        this.state = state;
        this.gameBoard = gameBoard;
    }

    // Getters and Setters
    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public char[][] getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(char[][] gameBoard) {
        this.gameBoard = gameBoard;
    }
}
