package net.pulga22.bulb.core.runnables;

import net.pulga22.bulb.core.GameInstance;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerRunnable extends BukkitRunnable {

    private final Runnable onTimeDone;
    private int timeLeft;

    public TimerRunnable(int seconds, Runnable onTimeDone) {
        this.timeLeft = seconds;
        this.onTimeDone = onTimeDone;
    }

    @Override
    public void run() {
        if (this.timeLeft == -1){
            cancel();
            return;
        }
        this.timeLeft--;
        if (this.timeLeft <= 0){
            cancel();
            this.onTimeDone.run();
        }
    }

    public TimerRunnable start(Plugin plugin){
        this.runTaskTimer(plugin, 0, 20);
        return this;
    }

}
