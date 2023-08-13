package net.pulga22.bulb.core.score.components.state;

import net.pulga22.bulb.core.states.GameState;

@FunctionalInterface
public interface StateFactory {
    String getStringPerState(GameState state);
}
