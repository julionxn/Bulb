package net.pulga22.minigamecore.core.score.components.counter;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;

public class CounterLimitComponent extends CounterComponent{

    private final int limit;

    public CounterLimitComponent(String prefix, int limit) {
        super(prefix);
        this.limit = limit;
    }

    public CounterLimitComponent(String prefix, int starting, int limit) {
        super(prefix, starting);
        this.limit = limit;
    }

    @Override
    public Score getScore() {
        return this.objective.getScore(prefix + ": " + ChatColor.WHITE + this.points + " / " + this.limit);
    }
}
