package online.madeofmagicandwires.tictac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Activity used to play a game of tic-tac-toe or Connect Fours
 */
public class MainActivity extends AppCompatActivity implements GameAdapter.OnGameOverListener {

    /**
     * The key to use when storing and getting a Game object from a Bundle
     * @see Game
     * @see #onCreate(Bundle)
     * @see #onRestoreInstanceState(Bundle)
     **/
    public static final String GAME_BUNDLE_KEY = "game";

    /** game instance to use in this activity **/
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // try to get game from previously saved state
        if(savedInstanceState != null) {
            game = (Game) savedInstanceState.getSerializable(GAME_BUNDLE_KEY);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Update Game instance if the board size setting has changed.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int boardSize = -1;
        if(prefs.contains("boardSize")) {
            try {
                // retrieve the board size setting and try to cast it to integer
                String boardSizeSetting = prefs.getString("boardSize", "3");
                Log.d("onResume", "boardSizeSetting is " + boardSizeSetting);
                //noinspection ConstantConditions
                boardSize = Integer.valueOf(boardSizeSetting);
            } catch (NumberFormatException e) {
                // if that can't be done, use default board size
                Log.e("onResume", "could not retrieve boardSize from preferences");
                boardSize = Game.DEFAULT_BOARD_SIZE;
            } finally {
                // create new game instance if game is not already present
                // or if the board size setting has been changed and is a playable value
                if(boardSize < Game.DEFAULT_BOARD_SIZE) {
                    boardSize = Game.DEFAULT_BOARD_SIZE;
                }
                if(game == null) {
                    game = new Game(boardSize);
                } else if(game.boardSize != boardSize) {
                    game = new Game(boardSize);
                }
            }
        }

        // draw board
        drawBoard();
    }

    /**
     * Draws the new state of the recyclerview game board.
     * @see #onCreate(Bundle)
     */
    private void drawBoard(){
        RecyclerView grid = findViewById(R.id.gameBoard);

        if(grid != null) {
            // we're using mimimum width because the width of the recyclerview is only calculated once the adapter is set.
            GameAdapter adapter = new GameAdapter(this, game, R.layout.gametile, grid.getMinimumWidth());
            adapter.setOnGameOverListener(this);

            grid.setAdapter(adapter);
            // TODO: use FixedGridLayoutManager
            // FixedGridLayoutManager gridManager = new FixedGridLayoutManager();
            // gridManager.setTotalColumnCount(game.boardSize);
            GridLayoutManager gridManager = new GridLayoutManager(this, game.boardSize);
            grid.setLayoutManager(gridManager);


            // Add onClick
            findViewById(R.id.resetBtn).setOnClickListener(new GameAdapter.ResetOnClickListener(adapter));

        } else {
            Log.e("BoardGrid",
                    getString(R.string.no_recyclerview_error_msg));
        }
    }

    /**
     * Creates the actionbar menu with the reset and settings action
     * @param menu supportActionBar instance to add items to
     * @return true;
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Handles click events on the actionbar menu items
     * @param item the selected options item
     * @return whether to trigger any other eventlisteners or consume the event;
     *         true for passing it on, false for consuming.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                RecyclerView board = findViewById(R.id.gameBoard);
                if(board != null && board.getAdapter() instanceof GameAdapter) {
                    ((GameAdapter) board.getAdapter()).resetBoard();
                }
                return true;
            case R.id.action_settings:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Opens the Settings screen
     */
    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /**
     * Saves the game state to be retrieved at a later point in the activity lifecycle
     * @param outState the bundle to save the state into which will be retrieved at a later point
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(GAME_BUNDLE_KEY, game);
    }

    /**
     * Called when the game has reached a conclusion
     *
     * @param gs          the gamestate containing the conclusion of the game
     * @param movesPlayed the number of moves played before conclusion was reached
     * @param roundsWon   the amount of wins for each player
     */
    @Override
    public void showWin(GameState gs, int movesPlayed, int[] roundsWon) {
        Log.d("Game Over!", gs.toString());
        RecyclerView board = findViewById(R.id.gameBoard);

        // If possible, create a snackbar and add an OnClick.
        if(board != null) {
            Snackbar snackbar = Snackbar.make(board, gs.toString(), Snackbar.LENGTH_LONG);
            if(board.getAdapter() instanceof GameAdapter)
            snackbar.setAction(R.string.reset_btn,
                    new GameAdapter.ResetOnClickListener(
                            ((GameAdapter)board.getAdapter())
                    )
            );
            // actually show snackbar
            snackbar.show();
        }


    }
}
