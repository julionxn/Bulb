package net.pulga22.minigamecore.core.score.components;

import net.pulga22.minigamecore.core.score.GameScoreComponent;
import net.pulga22.minigamecore.core.score.GameScoreboard;
import net.pulga22.minigamecore.core.score.errors.NoGetScoreOverrideRuntimeError;
import net.pulga22.minigamecore.core.score.errors.NotInitializedRuntimeError;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class SimpleComponent implements GameScoreComponent {

    protected Scoreboard scoreboard;
    protected Objective objective;
    private boolean initialized = false;
    private boolean visible = false;
    protected Score score;
    private int scoreInt;
    private GameScoreboard gameScoreboard;

    public SimpleComponent(){
    }

    @Override
    public final void init(int score, Scoreboard scoreboard, Objective objective, GameScoreboard gameScoreboard) {
        this.scoreboard = scoreboard;
        this.objective = objective;
        this.gameScoreboard = gameScoreboard;
        this.score = this.getScore();
        this.scoreInt = score;
        this.initialized = true;
    }

    /**
     * <p><b>NOT DEPRECATED:</b> This is method is not deprecated, deprecation means is NECESSARY to override.</p>
     * @return The score that is going to be displayed when started or updated.
     */
    @Deprecated
    public Score getScore(){
        return null;
    }

    /**
     * Show the component in the scoreboard.
     */
    @Override
    public final void show() {
        if (!initialized){
            throw new NotInitializedRuntimeError();
        }
        this.score = getScore();
        if (this.score == null){
            throw new NoGetScoreOverrideRuntimeError();
        }
        this.score.setScore(this.scoreInt);
        this.visible = true;
        this.gameScoreboard.callUpdate();
    }

    /**
     * Hides the component from the scoreboard.
     */
    @Override
    public final void hide() {
        if (!initialized){
            throw new NotInitializedRuntimeError();
        }
        this.resetScore();
        this.visible = false;
        this.gameScoreboard.callUpdate();
    }

    /**
     * NEEDED to be called when wanted to update the score.
     */
    public final void update(){
        this.resetScore();
        this.score = this.getScore();
        if (this.score == null){
            throw new NoGetScoreOverrideRuntimeError();
        }
        this.score.setScore(this.scoreInt);
        this.visible = true;
        this.gameScoreboard.callUpdate();
    }

    /**
     * Hides the component.
     */
    protected final void resetScore(){
        if (this.score == null){
            throw new NoGetScoreOverrideRuntimeError();
        }
        this.scoreboard.resetScores(this.score.getEntry());
    }

    public final boolean isVisible(){
        return this.visible;
    }


}
