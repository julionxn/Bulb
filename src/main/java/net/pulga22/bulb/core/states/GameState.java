package net.pulga22.bulb.core.states;

/**
 * A GameState represents in which fase of a game instance is.
 */
public enum GameState {
    NONE(0),
    PREPARED(1),
    PLAYING(2),
    FINISHING(3),
    FINISHED(4);

    public final int hierarchy;

    GameState(int hierarchy) {
        this.hierarchy = hierarchy;
    }

}
