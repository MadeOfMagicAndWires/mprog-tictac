package online.madeofmagicandwires.tictac;

import android.support.annotation.NonNull;

/**
 * GameState
 * enum keeping track of the state of a game.
 *
 * @author  Joost Bremmer
 * @version 1.0
 */

public enum GameState {
    IN_PROGRESS,
    PLAYER_ONE_WIN,
    PLAYER_TWO_WIN,
    DRAW;

    /**
     * Returns a Human readable String representing the state of the current object.
     * @return a human readable string representing the state of this instance
     */
    @Override
    public String toString() {
        switch (this) {
            case IN_PROGRESS:
                return "In progress.";
            case DRAW:
                return "Draw.";
            case PLAYER_ONE_WIN:
                return "Player One won.";
            case PLAYER_TWO_WIN:
                return "Player Two won.";
                default:
                    return "Unknown.";
        }
    }
}
