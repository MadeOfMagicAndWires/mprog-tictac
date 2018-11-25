package online.madeofmagicandwires.tictac;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

/**
 * GameAndroid
 * Subclass of Game containing methods required for drawing
 * and interacting with the board inside an Android activity.
 *
 * @author Joost Bremmer
 * @version 2.0
 */

public class GameAndroid extends Game implements View.OnClickListener {

    /**
     * Constructor. Creates a new instance of GameAndroid
     * @param boardSize the "cubic" amount of tiles created

     */
    public GameAndroid(int boardSize) {
        super(boardSize);

    }

    /**
     * Constructor. Creates a new instance of GameAndroid
     */
    public GameAndroid() {
        super();
    }

    /**
     * Returns a tile's position on the board from its tag.
     *
     * @param tile View; Must have a tag containing the tile's position of the board
     *                   in the format of 'row'-'column'. For example, '0-2' is the top center.
     * @return     int[]; position as an array, contains the row as the first number,
     *                                                   column as the second number.
     */
    private int[] tagToCoords(View tile) {

        return (new int[]{Character.getNumericValue(String.valueOf(tile.getTag()).charAt(0)),
                Character.getNumericValue(String.valueOf(tile.getTag()).charAt(2))});
    }

    /**
     * Returns a string based on the position on the board of a tile.
     * @param coords position as an array, contains the row as the first number,
     *               column as the second number.
     * @return String in the format of "row-column" based on the tile's position. ex. 0-1
     */
    private String coordsToTag(int[] coords) {
        return coords[0] + "-" + coords[1];
    }


    /**
     * Creates a board of boardSize^boardSize tiles and attaches it to a given GridLayout.
     *
     * Use <b>only</b> for the initial setup. to reset the board see {@link #resetBoard(GridLayout)}
     * @param boardGrid GridLayout for the board to be inserted into.
     * @see #boardSize
     * @see #onClick(View)
     */
    // TODO: Use RecyclerViews?
    public void drawBoard(GridLayout boardGrid, @NonNull Context context) {

        // Determining tile (min)height and (min)width based on total amount of tiles
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                300,
                context.getResources().getDisplayMetrics());
        float minSize = (size/ 7);

        // cleaning and initialising gridlayout
        boardGrid.removeAllViews();
        boardGrid.setRowCount(boardSize);
        boardGrid.setColumnCount(boardSize);
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate a new R.layout.gameTile for every tile and set various parameters
        for(int i=0;i<boardSize;i++) {
            for(int j=0;j<boardSize;j++){
                // inflate gameTile.xml
                Button gameTile = (Button) inflater.inflate(R.layout.gametile, null);
                
                // Set size of tiles based on board size.
                // minimum size corresponds to 50dp.
                gameTile.setMinimumHeight((int) minSize);
                gameTile.setMinimumWidth((int) minSize);
                gameTile.setHeight((int) (size / boardSize));
                gameTile.setWidth((int) (size /boardSize));

                // set #onClick as onClickListener
                gameTile.setOnClickListener(this);

                // send tile coordinates along in a tag; needed to keep track of the tiles' position
                // [row - col] ex. 0-1
                int[] tag = {i,j};
                gameTile.setTag(coordsToTag(tag));

                // if tile already belongs to a player,
                // set the corresponding text and make it unclickable
                switch (getTile(i,j)){
                    case PLAYER_ONE:
                        gameTile.setText(R.string.PLAYER_ONE);
                        gameTile.setClickable(false);
                        break;
                    case PLAYER_TWO:
                        gameTile.setText(R.string.PLAYER_TWO);
                        gameTile.setClickable(false);
                        break;
                }

                // also, if the game state is not in progress set ANY tile unclickable,
                // colour any winner
                if(getGameOver() != GameState.IN_PROGRESS) {
                    gameTile.setClickable(false);
                    colourTile(gameTile, getGameOver());
                }

                // finally, add tile to layout.
                boardGrid.addView(gameTile);

            }
        }

    }


    @Override
    /**
     * The onClickListener method of each tile, set in {@link #drawBoard(GridLayout)}
     *
     * Tries to claim the clicked tile for the current player,
     * then checks for the win condition and handles results
     *
     * @see View.OnClickListener
     * @see Game#choose(int, int)
     * @see Game#checkWinconditionReached(int, int)
     */
    public void onClick(View v) {
        // get tile coordinates from tag
        // [row, col]
        int[] tileCoords = tagToCoords(v);
        Button tile = (Button) v;


        // Play the move [row, col]
        TileState move = choose(tileCoords[0], tileCoords[1]);
        switch (move) {
            case PLAYER_ONE:
                tile.setText(R.string.PLAYER_ONE);
                tile.setClickable(false);
                break;
            case PLAYER_TWO:
                tile.setText(R.string.PLAYER_TWO);
                tile.setClickable(false);
                break;
            case INVALID:
                // this never happens
                Log.d("tictac","Invalid move");
                break;
        }

        // check if a win condition has been reached.
        GameState hasWon = checkWinconditionReached(tileCoords[0], tileCoords[1]);

        // Handle results; show the win or move on to the next move.
        switch (hasWon) {
            case IN_PROGRESS:
                // after move admin; increase played moves, next player etc.
                nextMove();
                break;
            case PLAYER_ONE_WIN:
                showWin(v, hasWon);
                break;
            case PLAYER_TWO_WIN:
                showWin(v, hasWon);
                break;
            case DRAW:
                showWin(v, hasWon);
                break;
        }

    }


    /**
     * Visually shows the winning player by means of pop up message and colouring the winner's tiles
     * @param v  view of clicked tile that triggered the win condition; needed because reasons.
     * @param gs GameState at the end of the round
     * @see Snackbar
     * @see #colourTile(Button, GameState)
     */
    private void showWin(View v, GameState gs) {
        // Find R.id.gameBoard
        // hacky as hell, but haven't found a better solution considering
        GridLayout board = v.getRootView().findViewById(R.id.gameBoard);
        // Create a snakcbar popup, with an onClick that fires #resetBoard()
        Snackbar snackbar = Snackbar.make(v, gs.toString(), Snackbar.LENGTH_LONG).
                setAction("RESET",
                        new View.OnClickListener(){
                            @Override public void onClick(View view) {
                                resetBoard((GridLayout)
                                        view.getRootView().findViewById(R.id.gameBoard));
                            }});

        // Make all tiles unclickable, "freezing" the board
        for(int i=0;i<board.getChildCount();i++){
            Button tile = (Button) board.getChildAt(i);
            tile.setClickable(false);
            colourTile(tile, gs);
        }

        // actually show snackbar
        snackbar.show();
    }


    /**
     * Colours a single tile green if it belongs to the winner, and otherwise black
     * @param gs   GameState containing the ending state of the round
     * @param tile Button of the current tile
     */
    public void colourTile(Button tile, GameState gs) {
        if(gs != GameState.DRAW && gs != GameState.IN_PROGRESS) {
            TileState winner = (gs == GameState.PLAYER_ONE_WIN) ?
                    TileState.PLAYER_ONE : TileState.PLAYER_TWO;
            int[] coords = tagToCoords(tile);

            if (getTile(coords[0], coords[1]) == winner) {
                tile.setTextColor(Color.parseColor("#008000"));
            } else {
                tile.setTextColor(Color.BLACK);
            }

        } else {
            tile.setTextColor(Color.BLACK);
        }
    }

    /**
     * Wipes the board state and makes things ready for a next round.
     * @param board GridLayout representing the board.
     */
    public void resetBoard(GridLayout board) {
        super.resetBoard();
        // clean up the gridlayout board,
        for(int i=0;i<board.getChildCount();i++){
            Button tile = (Button) board.getChildAt(i);
            tile.setText("");
            tile.setTextColor(Color.BLACK);
            tile.setClickable(true);
        }
    }
}
