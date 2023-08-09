package net.pulga22.minigamecore.core.score.components.status;

import net.pulga22.minigamecore.core.score.components.SimpleComponent;
import net.pulga22.minigamecore.core.states.GameState;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;

public class GameStateComponent extends SimpleComponent {

    private GameState state = GameState.NONE;

    public GameStateComponent(){
        super();
    }

    @Override
    public Score getScore() {
        switch (this.state){
            case NONE -> {
                return this.objective.getScore("Estado: ");
            }
            case PREPARED -> {
                return this.objective.getScore("Estado: " + ChatColor.GOLD + "Empezando");
            }
            case PLAYING -> {
                return this.objective.getScore("Estado: " + ChatColor.GOLD + "Jugando");
            }
            case FINISHING -> {
                return this.objective.getScore("Estado: " + ChatColor.GOLD + "Finalizando");
            }
            case FINISHED -> {
                return this.objective.getScore("Estado: " + ChatColor.GOLD + "Finalizado");
            }
        }
        return null;
    }

    public void setState(GameState state){
        this.state = state;
        this.update();
    }

    public GameState getState(){
        return this.state;
    }

}
