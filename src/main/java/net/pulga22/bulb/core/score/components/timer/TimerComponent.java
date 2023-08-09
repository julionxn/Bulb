package net.pulga22.bulb.core.score.components.timer;

import net.pulga22.bulb.core.score.components.SimpleComponent;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Score;

public class TimerComponent extends SimpleComponent {

    private final Plugin plugin;
    private final String prefix;
    public int timeLeft;
    private final TimerRunnableTask task = new TimerRunnableTask(this);
    private final Runnable onFinish;

    public TimerComponent(Plugin plugin, String prefix, int time) {
        this(plugin, prefix, time, null);
    }
    public TimerComponent(Plugin plugin, String prefix, int time, Runnable onFinish) {
        super();
        this.plugin = plugin;
        this.prefix = prefix;
        this.timeLeft = time;
        this.onFinish = onFinish;
    }

    @Override
    public Score getScore() {
        return this.objective.getScore(this.prefix + ": " + ChatColor.WHITE + this.timeLeft);
    }

    public void start(){
        this.task.runTaskTimer(this.plugin, 0, 20);
    }

    public Runnable getOnFinish(){
        return this.onFinish;
    }
}
