package net.pulga22.bulb.core.score.components.state;

import net.pulga22.bulb.core.score.components.SimpleComponent;
import net.pulga22.bulb.core.states.GameState;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;

import java.security.Provider;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameStateComponent extends SimpleComponent {

    private GameState state = GameState.NONE;
    private final String prefix;
    private final Function<GameState, String> function;

    public GameStateComponent(String prefix,  Function<GameState, String> function) {
        this.prefix = prefix;
        this.function = function;
    }

    @Override
    public Score getScore() {
        return this.objective.getScore(this.prefix + this.function.apply(this.state));
    }

    public void setState(GameState state){
        this.state = state;
        this.update();
    }

    public GameState getState(){
        return this.state;
    }

}
