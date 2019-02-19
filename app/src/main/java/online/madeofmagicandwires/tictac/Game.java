package online.madeofmagicandwires.tictac;


import java.io.Serializable;


/**
 * Game.java
 * Game class representing a data model for tic-tac-toe and methods for playing.
 *
 * @author Joost Bremmer
 * @version 2.0
 *
 */
public class Game implements Serializable {

    /** Default size of a tic-tac-toe board. **/
    static final public int DEFAULT_BOARD_SIZE = 3;

    /** Represents the board size used for specific instances **/
    public final int boardSize;
    /** Matrix representing the state of the board **/
    private TileState[][] board;
    /** Keeps track of the sum of moves played **/
    private int movesPlayed;
    /**
     * Array that keeps track of the amount of wins by both players **/
    private int[] wins;
    /**
     * Keeps track of the current state of the game **/
    private GameState gameOver;
    /**
     * Keeps track on whose turn it is **/
    private Boolean playerOneTurn;

    /**
     * Constructor. Creates an instance using a board size of n^n tiles.
     * @param boardSize the "cubic" amount of tiles created.
     */
    public Game(int boardSize) {
        this.boardSize = boardSize;
        initBoard();

    }

    /**
     * Constructor. Creates an instance using a board size of
     * DEFAULT_BOARD_SIZE^DEFAULT_BOARD_SIZE.
     * @see #DEFAULT_BOARD_SIZE
     *
     */
    public Game() {
        this.boardSize = DEFAULT_BOARD_SIZE;
        initBoard();
    }

    /**
     * Common actions taken for construction of an instance.
     * Should not be called directly outside constructors.
     */
    private void initBoard(){
        this.movesPlayed = 1;
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

    /**
     * Gets the amount moves played so far.
     * @see #movesPlayed
     * @return the sum of moves played by both players
     */
    public int getMovesPlayed() {
        return movesPlayed;
    }


    /**
     * Gets a boolean representing whose turn it is
     * @return Boolean, true if it's currently Player One's turn; false for Player Two.
     */
    public Boolean getPlayerOneTurn() {
        return playerOneTurn;
    }

    /**
     * Gets the state of the game.
     * @see GameState
     * @return GameState keeping track of the current state of the game.
     */
    public GameState getGameOver() {
        return gameOver;
    }

    /**
     * Gets an array representing the amount of wins by both players.
     *
     * First element represents the amount of rounds won by Player One.
     * Second element represents the amount of rounds won by Player Two.
     * @return int[2] the amount of wins by both players.
     */
    public int[] getWins() {
        return wins;
    }

    /**
     * Gets a representation of the current board.
     * @see TileState
     * @return a matrix containing every tile and its state
     */
    public TileState[][] getBoard() {
        return board;
    }

    /**
     * Sets the amount of moves played.
     * @param movesPlayed positive integer representing the total amount
     *                    of moves played
     */
    public void setMovesPlayed(int movesPlayed) {
        this.movesPlayed = Math.abs(movesPlayed);
    }

    /**
     * updates the current state of the board.
     * @see TileState
     * @param board new TileState matrix representing the board.
     */
    public void setBoard(TileState[][] board) {
        this.board = board;
    }

    /**
     * Updates the current state of the game.
     * @see GameState
     * @param gameOver GameState representing the new state of the game.
     */
    public void setGameOver(GameState gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * updates whose turn it is.
     * @param playerOneTurn Boolean keeping track whose turn it is;
     *                      true for Player One, false for Player Two
     */
    public void setPlayerOneTurn(Boolean playerOneTurn) {
        this.playerOneTurn = playerOneTurn;
    }

    /**
     * Updates the amount of wins of each player
     * @param wins int[] containing two elements;
     *             wins[0] for amount of wins for Player One,
     *             wins[1] for amount of wins for Player Two
     */
    public void setWins(int[] wins) {
        if(wins.length == 2){
            this.wins = wins;
        }
    }

    /**
     * Updates the amount of wins for each player
     * @param winsPlayerOne amount of wins for Player One
     * @param winsPlayerTwo amount of wins for Player Two
     */
    public void setWins(int winsPlayerOne, int winsPlayerTwo) {
        this.wins[0] = Math.abs(winsPlayerOne);
        this.wins[2] = Math.abs(winsPlayerTwo);
    }

    /**
     * Adds a win for the corresponding winning player based on GameState.
     * @see GameState
     * @param gs GameState endstate of the current round.
     */
    private void addWin(GameState gs) {
        if(gs == GameState.PLAYER_ONE_WIN) { this.wins[0]++;}
        else if (gs == GameState.PLAYER_TWO_WIN) {this.wins[1]++;}
    }

    /**
     * Tries to claim a tile for the current player
     * @param row row of the tile in the gameBoard matrix
     * @param col column of the tile in the gameBoard matrix
     * @return the updated TileState of said tile, or TileState.INVALID if the move was impossible.
     * @see TileState
     */
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
     * @see GameState
     * @return the current state of the game after checking win conditions
     */
    public GameState checkWinconditionReached(int row, int col) {

        // too little moves played, win condition cannot have been reached yet
        // keep in mind that movesPlayed has not been updated at this point.
        if(movesPlayed < ((boardSize*2)-2)) {
            return GameState.IN_PROGRESS;
        }

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
                addWin(gameOver);
                return gameOver;
            }
        }

        // otherwise check how many moves were played
        // (note that the move counter will not yet have been updated)
        // if moves are n+1^n+1 then it's a draw; else, there are still moves possible.

        if (this.movesPlayed+1 == boardSize*boardSize) {
            gameOver = GameState.DRAW;
        } else {
            gameOver = GameState.IN_PROGRESS;
        }

        return gameOver;

    }

    /**
     * Get the TileState of a specific tile
     * @see TileState
     * @param row row of the tile
     * @param col column of the tile
     * @return current TileState of the specific tile
     */
    public TileState getTile(int row, int col) {
        return board[row][col];
    }


    /**
     * Updates the various variables keeping track of which turn it is,
     * signifying the next players move.
     *
     * @see #movesPlayed
     * @see #playerOneTurn
     *
     * Should be called BEFORE the next move is played, but AFTER win condition is checked.
     * @return the sum amount of moves played by both players.
     */
    public int nextMove(){
        this.movesPlayed++;
        this.playerOneTurn = !playerOneTurn;

        return movesPlayed;

    }

    /**
     * Resets the board and all state tracking variables for a new round.
     * @see #movesPlayed
     * @see #playerOneTurn
     * @see #boardSize
     * @see #gameOver
     */
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


    /**
     * Returns a Human readable String representing the state of the current object.
     * @return Human readable String representing the current instance.
     */
    @Override
    public String toString() {
        return "Playing with a board of " + boardSize + "*" + boardSize+"." +
                "We've played " + (wins[0] + wins[1]) + " Rounds." +
                "X won " + wins[0] + "\t|\t" + "O won " + wins[1];
    }

}
