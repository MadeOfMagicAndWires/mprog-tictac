package online.madeofmagicandwires.tictac;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class GameAndroid extends Game {

    private GameAdapter adapter;

    /**
     * Standard constructor
     * @param boardSize the size of the board
     */
    public GameAndroid(int boardSize) {
        super(boardSize);
    }

    /**
     *
     * @param boardGrid
     * @param c
     */
    public void drawBoard(@NonNull Context c, @NonNull RecyclerView boardGrid) {
        if(adapter == null) {
            adapter = new GameAdapter(c, this, GameAdapter.DEFAULT_LAYOUT);
        }

        boardGrid.setAdapter(adapter);
        GridLayoutManager grid = new GridLayoutManager(c, boardSize);
        boardGrid.setLayoutManager(grid);
    }
}
