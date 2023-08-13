package net.pulga22.bulb.core.score.components.state;

import net.pulga22.bulb.core.score.components.SimpleComponent;
import net.pulga22.bulb.core.states.GameState;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;

import java.security.Provider;
import java.util.function.Consumer;

public class GameStateComponent extends SimpleComponent {

    private GameState state = GameState.NONE;
    private final String prefix;
    private final StateFactory factory;

    public GameStateComponent(String prefix,  StateFactory factory) {
        super();
        this.prefix = prefix;
        this.factory = factory;
    }

    @Override
    public Score getScore() {
        return this.objective.getScore(this.prefix + this.factory.getStringPerState(this.state));
    }

    public void setState(GameState state){
        this.state = state;
        this.update();
    }

    public GameState getState(){
        return this.state;
    }

}
