package online.madeofmagicandwires.tictac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /**
     * OnClick Listener to manually reset the game board
     */
    public static class ResetOnClickListener implements View.OnClickListener {

        private GameAdapter adapter;

        /**
         * Standard constructor
         * @param adapter the GameAdapter linked to the current game
         */
        public ResetOnClickListener(GameAdapter adapter) {
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

    public static final String GAME_BUNDLE_KEY = "game";

    private static Game game;

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
                if (boardSizeSetting != null) {
                    Log.d("onResume", "boardSizeSetting is " + boardSizeSetting);
                    boardSize = Integer.valueOf(boardSizeSetting);
                }
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
    public void drawBoard(){
        RecyclerView grid = findViewById(R.id.gameBoard);

        if(grid != null) {
            // we're using mimimum width because the width of the recyclerview is only calculated once the adapter is set.
            GameAdapter adapter = new GameAdapter(this, game, R.layout.gametile, grid.getMinimumWidth());

            grid.setAdapter(adapter);
            // TODO: use FixedGridLayoutManager
            GridLayoutManager gridManager = new GridLayoutManager(this, game.boardSize);
            grid.setLayoutManager(gridManager);


            // Add onClick
            findViewById(R.id.resetBtn).setOnClickListener(new ResetOnClickListener(adapter));

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
     * @return
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
    public void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /**
     * Saves the game state to be retrieved at a later point in the activity lifecycle
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(GAME_BUNDLE_KEY, game);
    }
}
