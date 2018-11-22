package online.madeofmagicandwires.tictac;

public enum GameState {
    IN_PROGRESS,
    PLAYER_ONE_WIN,
    PLAYER_TWO_WIN,
    DRAW;

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
        }
        return null;
    }
}
