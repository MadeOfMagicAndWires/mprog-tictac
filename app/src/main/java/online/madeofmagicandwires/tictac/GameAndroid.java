package online.madeofmagicandwires.tictac;


import android.content.Context;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import android.util.Log;

import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.GridLayout;

import java.util.HashMap;


public class GameAndroid extends Game implements View.OnClickListener {

    private final Context CURRENT_CONTEXT;
    private final float SIZE;
    private final float MINSIZE;

    public GameAndroid(int boardSize, @NonNull  Context context) {
        super(boardSize);
        this.CURRENT_CONTEXT = context;
        this.SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                300,
                CURRENT_CONTEXT.getResources().getDisplayMetrics());
        this.MINSIZE = (SIZE / 7);


    }

    public GameAndroid(@NonNull Context context) {
        super();
        this.CURRENT_CONTEXT = context;
        this.SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                300,
                CURRENT_CONTEXT.getResources().getDisplayMetrics());
        this.MINSIZE = (SIZE / 7);


    }

    /**
     * Read a views position from its tag.
     *
     * @param tile View; Must have a tag containing the tile's position of the board
     *                   in the format of 'row'-'column'. For example, '0-2' is the top center.
     * @return     int[]; position as an array, contains the row as the first number,
     *                                                   column as the second number.
     */
    public int[] tagToCoords(View tile) {

        return (new int[]{Character.getNumericValue(String.valueOf(tile.getTag()).charAt(0)),
                Character.getNumericValue(String.valueOf(tile.getTag()).charAt(2))});
    }

    public String coordsToTag(int[] coords) {
        return coords[0] + "-" + coords[1];
    }

    // TODO: Use RecyclerViews?

    public void drawBoard(GridLayout boardGrid) {

        boardGrid.removeAllViews();
        boardGrid.setRowCount(boardSize);
        boardGrid.setColumnCount(boardSize);
        LayoutInflater inflater = LayoutInflater.from(CURRENT_CONTEXT);

        for(int i=0;i<boardSize;i++) {
            for(int j=0;j<boardSize;j++){
                Button gameTile = (Button) inflater.inflate(R.layout.gametile, null);
                
                // Set size of tiles based on amount. 
                // minimum size is 50dp.
                gameTile.setMinimumHeight((int) MINSIZE);
                gameTile.setMinimumWidth((int) MINSIZE);
                gameTile.setHeight((int) (SIZE / boardSize));
                gameTile.setWidth((int) (SIZE/boardSize));

                gameTile.setOnClickListener(this);

                // send tile coordinates along in a tag
                // [row - col] ex. 0-1
                int[] tag = {i,j};
                gameTile.setTag(coordsToTag(tag));

                switch (getTile(i,j)){
                    case PLAYER_ONE:
                        gameTile.setText(R.string.PLAYER_ONE);
                        break;
                    case PLAYER_TWO:
                        gameTile.setText(R.string.PLAYER_TWO);
                }

                boardGrid.addView(gameTile);

            }
        }

    }

    /**
     * Colours the tiles of the winning player green.
     * @param board GridLayout representing the board.
     */
    public void colourTiles(GameState gs, GridLayout board) {
        HashMap<Integer, Integer[]> winningTiles = getWinningPlayerTiles();
        for(Integer[] coords: winningTiles.values()){
            int[] intCoords = {(int) coords[0], (int) coords[1]};
            Button tile = board.findViewWithTag(coordsToTag((intCoords)));


            tile.setTextColor(Color.parseColor("#008000"));
        }
    }

    public void showWin(View v, GameState gs) {
        // hacky as hell, but haven't found a better solution
        GridLayout board = v.getRootView().findViewById(R.id.gameBoard);
        Snackbar snackbar = Snackbar.make(board, gs.toString(), Snackbar.LENGTH_LONG).
                setAction("RESET",
                        new View.OnClickListener(){
                            @Override public void onClick(View view) {
                                resetBoard((GridLayout)
                                        view.getRootView().findViewById(R.id.gameBoard));
                            }});

        // Make all tiles unclickable.
        for(int i=0;i<board.getChildCount();i++){
            View tile = board.getChildAt(i);
            tile.setClickable(false);
        }

        if(gs != GameState.DRAW){
            colourTiles(gs, board);
        }

        snackbar.show();
    }


    /**
     * Resets the board state
     * @param board GridLayout representing the board.
     */
    public void resetBoard(GridLayout board) {
        super.resetBoard();
        // hacky as hell, but haven't found a better solution.
        for(int i=0;i<board.getChildCount();i++){
            Button tile = (Button) board.getChildAt(i);
            tile.setText("");
            tile.setTextColor(Color.BLACK);
            tile.setClickable(true);
        }
    }

    @Override
    public void onClick(View v) {
        // get tile coordinates from tag
        // [row, col]
        int[] tileCoords = tagToCoords(v);
        Button tile = (Button) v;
        Log.d("tictac", "tile " + tileCoords[0] + "-" + tileCoords[1]);

        // Play the move
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
                Log.d("tictac","Invalid move");
                break;
        }

        // check if a win condition has been reached.
        GameState hasWon = checkWinconditionReached(tileCoords[0], tileCoords[1]);

        switch (hasWon) {
            case IN_PROGRESS:
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

        // after move admin; increase played moves, next player etc.
        nextMove();
        Log.d("tictac", hasWon.toString());

    }





}
