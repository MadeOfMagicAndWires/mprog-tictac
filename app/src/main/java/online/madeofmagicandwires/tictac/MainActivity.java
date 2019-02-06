package online.madeofmagicandwires.tictac;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

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
    private GameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            game = (Game) savedInstanceState.getSerializable(GAME_BUNDLE_KEY);
            if(game == null) {
                game = new Game(3);
            }
        } else  {
            game = new Game(3);
        }

        RecyclerView grid = findViewById(R.id.gameBoard);
        if(grid != null) {
            if(adapter == null) {
                adapter = new GameAdapter(this, game, R.layout.gametile);
            }
            grid.setAdapter(adapter);
            if(grid.getLayoutManager() == null)  {
                GridLayoutManager gridManager = new GridLayoutManager(this, game.boardSize);
                grid.setLayoutManager(gridManager);
            }

            // Add onClick
            findViewById(R.id.resetBtn).setOnClickListener(new ResetOnClickListener(adapter));

        } else {
            Log.e("BoardGrid",
                    "Could not find the board grid; please ensure there is a RecyclerView with the id R.id.gameBoard");
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(GAME_BUNDLE_KEY, game);
    }
}
