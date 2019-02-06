package online.madeofmagicandwires.tictac;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

public class GameViewHolder extends RecyclerView.ViewHolder {

    public Button tile;
    private TileState state;


    public GameViewHolder(@NonNull View itemView, TileState state) {
        super(itemView);
        this.tile = (Button) itemView;
        this.state = state;
    }

    public TileState getState() {
        return state;
    }

    /**
     * Sets the TileState of this Tile, as well as representing the new state on the tilegrid.
     * @param state the new state of this instance
     */
    public void setState(TileState state) {
        switch (state) {
            case PLAYER_ONE:
                tile.setText(R.string.PLAYER_ONE);
                break;
            case PLAYER_TWO:
                tile.setText(R.string.PLAYER_TWO);
                break;
            case BLANK:
                tile.setText(R.string.BLANK);
                break;
        }
        this.state = state;

    }


    /**
     * Colours this tile green if it belongs to the winner, and otherwise black
     * @param gs   GameState containing the ending state of the round
     */
    public void colourTile(GameState gs) {
        if(gs != GameState.DRAW && gs != GameState.IN_PROGRESS) {
            TileState winner = (gs == GameState.PLAYER_ONE_WIN) ?
                    TileState.PLAYER_ONE : TileState.PLAYER_TWO;
            if(state == winner) {
                    tile.setTextColor(tile.getResources().getColor(R.color.darkGreen, null));
            } else {
                tile.setTextColor(tile.getResources().getColor(R.color.black, null));
            }
        }
    }


}
