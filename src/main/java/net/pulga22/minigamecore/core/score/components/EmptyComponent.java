package net.pulga22.minigamecore.core.score.components;

import net.pulga22.minigamecore.core.score.GameScoreComponent;
import net.pulga22.minigamecore.core.score.GameScoreboard;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class EmptyComponent implements GameScoreComponent {

    protected Scoreboard scoreboard;
    protected Objective objective;
    protected Score score;
    private int scoreInt;

    @Override
    public Score getScore() {
        return this.score;
    }

    @Override
    public final void show() {
        this.score.setScore(this.scoreInt);
    }

    @Override
    public final void hide() {
        this.scoreboard.resetScores(this.score.getEntry());
    }

    @Override
    public final void init(int score, Scoreboard scoreboard, Objective objective, GameScoreboard gameScoreboard) {
        this.scoreboard = scoreboard;
        this.objective = objective;
        int times = score < 0 ? 10 + -score : score;
        this.score = objective.getScore(" ".repeat(times));
        this.scoreInt = score;
        this.show();
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
