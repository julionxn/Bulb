package net.pulga22.minigamecore.core.score.components.timer;

import org.bukkit.scheduler.BukkitRunnable;

public class TimerRunnableTask extends BukkitRunnable {

    private final TimerComponent component;

    public TimerRunnableTask(TimerComponent component) {
        this.component = component;
    }

    @Override
    public void run() {
        this.component.timeLeft--;
        if (this.component.timeLeft <= 0){
            Runnable onFinish = this.component.getOnFinish();
            if (onFinish != null){
                onFinish.run();
            }
            this.cancel();
            return;
        }
        component.update();
    }
}
