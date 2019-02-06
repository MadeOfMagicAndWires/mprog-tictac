package online.madeofmagicandwires.tictac;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

/**
 * Main and only activity of this app
 *
 * @author Joost Bremmer
 * @version 2.0
 * @deprecated
 * @see MainActivity
 */
public class MainActivityOld extends AppCompatActivity {

    /** GameAndroidOld instance **/
    GameAndroidOld game;

    @Override
    /**
     * It's ya boi! Creates or recovers GameAndroidOld instance on Activity creation,
     * then draws the board
     *
     * @see AppCompatActivity#onCreate(Bundle, PersistableBundle)
     * @see GameAndroidOld#drawBoard(GridLayout)
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);

        if(savedInstanceState != null) {
            game = (GameAndroidOld) savedInstanceState.getSerializable("game");

        } else {
            game = new GameAndroidOld(GameAndroidOld.DEFAULT_BOARD_SIZE);
        }

        // drawing board
        Log.d("tictac", "drawing board ");
        game.drawBoard((GridLayout) findViewById(R.id.gameBoard), this);

        // Finally, set OnClickListener on the reset button to call GameAndroidOld#resetBoard()
        findViewById(R.id.resetBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.resetBoard((GridLayout) findViewById(R.id.gameBoard));
            }
        });

    }

    @Override
    /**
     * Saves GameAndroidOld instance on suspension. Also doesn't work
     *
     * @see AppCompatActivity#onSaveInstanceState(Bundle)
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("game", game);
    }
}
