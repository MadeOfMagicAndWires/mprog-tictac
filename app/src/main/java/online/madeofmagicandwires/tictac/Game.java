package online.madeofmagicandwires.tictac;


import android.util.SparseIntArray;

import java.io.Serializable;
import java.util.HashMap;


public class Game implements Serializable {

    final private int DEFAULT_BOARD_SIZE = 3;

    public final int boardSize;
    private TileState[][] board;
    private int movesPlayed;
    private int[] wins;
    private GameState gameOver;
    private Boolean playerOneTurn;

    public Game(int boardSize) {
        this.boardSize = boardSize;
        initBoard();

    }

    public Game() {
        this.boardSize = DEFAULT_BOARD_SIZE;
        initBoard();
    }

    public void initBoard(){
        this.movesPlayed = 0;
        this.gameOver = GameState.IN_PROGRESS;
        this.wins = new int[] {0,0};
        this.playerOneTurn = true;

        this.board = new TileState[boardSize][boardSize];
        for(int i=0;i<this.boardSize;i++) {
            for(int j=0; j<this.boardSize;j++){
                this.board[i][j] = TileState.BLANK;
            }
        }
    }

    public TileState choose(int row, int col) {

        switch(board[row][col]) {
            case BLANK:
                if (playerOneTurn) {
                    board[row][col] = TileState.PLAYER_ONE;
                } else {
                    board[row][col] = TileState.PLAYER_TWO;
                }
                return board[row][col];

            default:
                return TileState.INVALID;
        }
    }


    /**
     * Checks if any player has reached a win condition.
     * @return GameState the current state of the game
     */
    public GameState checkWinconditionReached(int row, int col) {

        /*
         * keeps a track of how many tiles are owned by the same player
         * tileOwnerCount[0] represents rows
         * tileOwnerCount[1] represents columns
         * tileOwnerCount[2] represents diagonals
         * tileOwnerCount[3] represents diagonals, but backwards.
         */
        int tileOwnerCount[] = {0,0,0,0};
        TileState player = (playerOneTurn) ? TileState.PLAYER_ONE : TileState.PLAYER_TWO;

        for(int i=0;i<boardSize;i++) {
            // counts row tiles owned by current player.
            if(board[row][i] == player) { tileOwnerCount[0]++; }
            // counts column tiles owned by current player
            if(board[i][col] == player) { tileOwnerCount[1]++; }
            // counts diagonal tiles owned by current player
            if(board[i][i] == player) { tileOwnerCount[2]++; }
            // counts reverse diagonal tiles owned by current player
            if(board[i][((board.length-1)-i)] == player) { tileOwnerCount[3]++; }

        }


        // check if player has enough tiles to win.
        for(int i=0;i<tileOwnerCount.length;i++) {
            // check if anyone has won
            if (tileOwnerCount[i] == boardSize) {
                gameOver = (playerOneTurn) ? GameState.PLAYER_ONE_WIN : GameState.PLAYER_TWO_WIN;
                return gameOver;
            }
        }

        // otherwise check how many moves were played
        // if moves are n^n then it's a draw; else it's still in progress.

        if (this.movesPlayed+1 == boardSize*boardSize) {
            gameOver = GameState.DRAW;
;
        } else {
            gameOver = GameState.IN_PROGRESS;
        }

        return gameOver;

    }

    public TileState getTile(int row, int col) {
        return board[row][col];
    }


    public int nextMove(){
        this.movesPlayed++;
        this.playerOneTurn = !playerOneTurn;

        return movesPlayed;

    }



    public HashMap<Integer, Integer[]> getWinningPlayerTiles() {
        TileState winner = (gameOver == GameState.PLAYER_ONE_WIN) ?
                TileState.PLAYER_ONE : TileState.PLAYER_TWO;
        HashMap<Integer, Integer[]> winningTiles = new HashMap<>();
        int index = 0;
        for(int row=0;row<boardSize;row++){
            for(int col=0;col<boardSize;col++){
                if(board[row][col] == winner){
                    winningTiles.put(index, new Integer[] {new Integer(row), new Integer(col)});
                    index++;
                }
            }
        }
        return winningTiles;
    }

    public void resetBoard() {
        for(int i=0;i<boardSize;i++) {
            for(int j=0;j<boardSize;j++){
                board[i][j] = TileState.BLANK;
                movesPlayed = 0;
                gameOver = GameState.IN_PROGRESS;
                playerOneTurn = true;
            }
        }
    }




}
