package net.pulga22.bulb.core.score;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public interface GameScoreComponent {
    Score getScore();
    void show();
    void hide();
    void init(int score, Scoreboard scoreboard, Objective objective, GameScoreboard gameScoreboard);
    boolean isVisible();
}
