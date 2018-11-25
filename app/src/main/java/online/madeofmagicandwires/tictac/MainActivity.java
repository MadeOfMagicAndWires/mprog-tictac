package online.madeofmagicandwires.tictac;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import java.io.IOException;

/**
 * Main and only activity of this app
 *
 * @author Joost Bremmer
 * @version 2.0
 */
public class MainActivity extends AppCompatActivity {

    /** GameAndroid instance **/
    GameAndroid game;

    @Override
    /**
     * It's ya boi! Creates or recovers GameAndroid instance on Activity creation,
     * then draws the board
     *
     * @see AppCompatActivity#onCreate(Bundle, PersistableBundle)
     * @see GameAndroid#drawBoard(GridLayout)
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            game = (GameAndroid) savedInstanceState.getSerializable("game");

        } else {
            game = new GameAndroid(GameAndroid.DEFAULT_BOARD_SIZE);
        }

        // drawing board
        Log.d("tictac", "drawing board ");
        game.drawBoard((GridLayout) findViewById(R.id.gameBoard), this);

        // Finally, set OnClickListener on the reset button to call GameAndroid#resetBoard()
        findViewById(R.id.resetBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.resetBoard((GridLayout) findViewById(R.id.gameBoard));
            }
        });

    }

    @Override
    /**
     * Saves GameAndroid instance on suspension. Also doesn't work
     *
     * @see AppCompatActivity#onSaveInstanceState(Bundle)
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("game", game);
    }
}
