package online.madeofmagicandwires.tictac;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameAdapter extends RecyclerView.Adapter<GameViewHolder> {

    /**
     * Callback interface to be called when the adapter moves on to the next move
     */
    interface OnNextMoveListener {
        /**
         * Called <b>after</b> the adapter has moved on to the next turn
         * @param gs the new game state
         * @param playerOneTurn the player whose the next turn belongs to
         * @param newTileState the new tile state of the previously played tile
         */
        void onNewMove(GameState gs, boolean playerOneTurn, TileState newTileState);
    }

    /**
     * Callback interface to be called when the game has ended
     */
    interface OnGameOverListener {
        /**
         * Called when the game has reached a conclusion
         * @param gs the gamestate containing the conclusion of the game
         * @param movesPlayed the number of moves played before conclusion was reached
         * @param roundsWon the amount of wins for each player
         */
        void showWin(GameState gs, int movesPlayed, int[] roundsWon);
    }

    /**
     * OnClick Listener to manually reset the game board
     */
    public static class ResetOnClickListener implements View.OnClickListener {

        private GameAdapter adapter;

        /**
         * Standard constructor
         * @param adapter the GameAdapter linked to the current game
         */
        ResetOnClickListener(GameAdapter adapter) {
            this.adapter = adapter;
        }


        /**
         * Called when a view has been clicked. Resets the board of the Game Adapter
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            adapter.resetBoard();
        }
    }

    /**
     * OnClick listener to be linked to each specific tile.
     */
    private static class PlayNextMoveOnClick implements View.OnClickListener {


        private GameAdapter adapter;

        PlayNextMoveOnClick(@NonNull GameAdapter adapter) {
            this.adapter = adapter;
        }
        /**
         * Plays the tile corresponding view has been clicked and moves on to the next move.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Point coords = (Point) v.getTag(R.id.coordinates);
            if(coords != null) {
                TileState move = adapter.requestMove(coords.y, coords.x);
                if(move != TileState.INVALID) {
                    Log.d("PlayNextMoveOnClick",
                            move.toString() + " played move " +
                                    (coords.y + 1) + "-" + (coords.x + 1));
                    v.setClickable(false);

                    // check if a win condition has been reached.
                    GameState hasWon = adapter.checkWinConditionReached(coords.y, coords.x);

                    // Handle results; show the win or move on to the next move.
                    switch (hasWon) {
                        case IN_PROGRESS:
                            // after move admin; increase played moves, next player etc.
                            adapter.nextMove(move);
                            break;
                        default:
                            break;
                    }
                }
            }
        }

    }

    /** the default tile layout file **/
    static final @LayoutRes int DEFAULT_LAYOUT = R.layout.gametile;


    private final int TILE_SIZE;

    /** game instance used to determine board state **/
    private Game mGame;
    /** inflates the layout file to be used **/
    private LayoutInflater inflater;
    /** layout file to be inflated **/
    private @LayoutRes int layout;
    /** the callback listener to use **/
    private OnNextMoveListener nextMoveListener;
    private OnGameOverListener gameOverListener;
    private PlayNextMoveOnClick tileOnClickListener;



    /**
     * Most specific constructor
     * @param c Activity layout
     * @param game game instance representing the current gamestate
     * @param layoutFile layout file to be used
     * @param parentWidth the total width of the parent (Recycler)view
     */
    public GameAdapter(@NonNull Context c, @NonNull Game game, @LayoutRes int layoutFile, int parentWidth) {
        this.mGame = game;
        this.inflater = LayoutInflater.from(c);
        this.layout = layoutFile;
        this.TILE_SIZE = (int) Math.floor(parentWidth / (double) game.boardSize);
    }

    /**
     * Constructor leaving out the total pixel size of the parent (Recycler)View;
     * will use {@link R.dimen#board_grid_min_size} as default fallback.
     * @param c Context needed to retrieve a LayoutInflator
     * @param game game instance representing the current gamestate
     * @param layoutFile layout file to be used
     */
    public GameAdapter(@NonNull Context c, @NonNull Game game, @LayoutRes int layoutFile) {
        this(c, game, layoutFile, c.getResources().getDimensionPixelSize(R.dimen.board_grid_min_size));

    }

    /**
     * Constructor leaving out the layout xml and total pixel size of the parent (Recycler)View;
     * will use {@link #DEFAULT_LAYOUT} and {@link R.dimen#board_grid_min_size} as default fallback
     * @param c Context needed to retrieve a LayoutInflator
     * @param game game instance representing the current gamestate
     */
    public GameAdapter(@NonNull Context c, @NonNull Game game) {
        this(c, game, DEFAULT_LAYOUT, c.getResources().getDimensionPixelSize(R.dimen.board_grid_min_size));
    }


    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = inflater.inflate(layout, parent, false);
        Point coords = getCoordinatesFromAdapterPosition(i);
        return new GameViewHolder(v, mGame.getTile(coords.x, coords.y));
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder viewHolder, int i) {
        // set the size of the tile based on how big the board is
        int calculatedTileSize = (TILE_SIZE > viewHolder.tile.getMinWidth()) ? TILE_SIZE : viewHolder.tile.getMinWidth();
        viewHolder.tile.setHeight(calculatedTileSize);
        viewHolder.tile.setWidth(calculatedTileSize);

        // retrieve tile coordinates
        Point coords = getCoordinatesFromAdapterPosition(i);


        // set data according to tile
        viewHolder.setState(mGame.getTile(coords.y, coords.x));
        viewHolder.colourTile(mGame.getGameOver());

        // finally set the coordinates as tag
        viewHolder.tile.setTag(R.id.coordinates, coords);

        // Set or remove the onclick listener based on whether the tile has been played already
        if(mGame.getGameOver() == GameState.IN_PROGRESS && mGame.getTile(coords.y, coords.x) == TileState.BLANK) {
            if(tileOnClickListener == null) {
                tileOnClickListener = new PlayNextMoveOnClick(this);
            }
            viewHolder.itemView.setOnClickListener(tileOnClickListener);
        } else {
            viewHolder.tile.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return (mGame.boardSize * mGame.boardSize);
    }

    /**
     * Retrieves the item's 2D grid coordinates based on its 1D position (assuming the grid has the same boardSize as mGame)
     * @param pos position
     * @see #mGame
     * @return the item's grid coordinates held in a Point (for convenience's sake)
     */
    private Point getCoordinatesFromAdapterPosition(int pos) {
        int x = (int) Math.floor((pos/(double) mGame.boardSize));
        int y = (pos % mGame.boardSize);
        return new Point(x, y);
    }

    public void resetBoard(){
        mGame.resetBoard();
        notifyDataSetChanged();
    }

    /**
     * Selects a tile for the current player
     * @param tileRow the row number of the last played tile counting from 0
     * @param tileCol the col number of the last played tile counting from 0
     * @return the new state of the tile, represented as a TileState
     * @see TileState
     */
    TileState requestMove(int tileRow, int tileCol) {
        TileState tileState = mGame.choose(tileRow, tileCol);
        notifyDataSetChanged();
        return tileState;
    }

    /**
     * Checks if a win condition has been reached for either player or whether the game has ended
     * @param tileRow the row number of the last played tile counting from 0
     * @param tileCol the col number of the last played tile counting from 0
     * @return the current state of the game, as a GameState enum
     * @see GameState
     */
    GameState checkWinConditionReached(int tileRow, int tileCol) {
        GameState gs =  mGame.checkWinconditionReached(tileRow, tileCol);
        if(gs != GameState.IN_PROGRESS) {
            if(gameOverListener != null) {
                gameOverListener.showWin(gs, mGame.getMovesPlayed(), mGame.getWins());
            }
        }
        return gs;
    }

    /**
     * Lets the Adapter know to move on to the next player's move
     * and fires any provided OnNextMoveListener callbacks
     *
     * @see OnNextMoveListener
     */
    void nextMove(TileState previouslyPlayedTile) {
        mGame.nextMove();
        if(nextMoveListener != null) {
            nextMoveListener.onNewMove(
                    mGame.getGameOver(),
                    mGame.getPlayerOneTurn(),
                    previouslyPlayedTile);
        }
        notifyDataSetChanged();
    }


    /**
     * Links a OnNextMoveListener callback to this instance
     * @param listener an implementation of the OnNextMoveListener callback interface
     * @see OnNextMoveListener
     */
    public void setOnNextMoveListener(OnNextMoveListener listener) {
        this.nextMoveListener = listener;
    }

    /**
     * Links a OnGameOverListener callback to this instance
     * @param listener an implementation of the OnGameOverListener callback interface
     * @see OnGameOverListener
     */
    public void setOnGameOverListener(OnGameOverListener listener) {
        this.gameOverListener = listener;
    }

}