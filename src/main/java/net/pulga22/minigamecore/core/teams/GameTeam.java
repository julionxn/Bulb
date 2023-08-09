package net.pulga22.minigamecore.core.teams;

import net.pulga22.minigamecore.core.score.GameScoreboard;
import net.pulga22.minigamecore.core.score.components.counter.CounterComponent;
import org.bukkit.scoreboard.Team;

/**
 * An auxiliary class to create a team and its respective component in the scoreboard.
 */
public class GameTeam {

    private final Team team;
    private final CustomTeam customTeam;
    private final CounterComponent counterComponent;

    public GameTeam(GameScoreboard gameScoreboard, CustomTeam customTeam){
        this.team = gameScoreboard.registerTeam(customTeam);
        this.customTeam = customTeam;
        this.counterComponent = CounterComponent.of(customTeam);
    }

    public Team team(){
        return this.team;
    }

    public CustomTeam customTeam(){
        return this.customTeam;
    }

    public CounterComponent counter(){
        return this.counterComponent;
    }

}
