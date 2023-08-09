package net.pulga22.minigamecore.core.runnables;

import net.pulga22.minigamecore.core.GameInstance;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerRunnable<T extends Plugin> extends BukkitRunnable {

    private final GameInstance<T> gameInstance;
    private int timeLeft;

    public TimerRunnable(GameInstance<T> gameInstance) {
        this.gameInstance = gameInstance;
        this.timeLeft = gameInstance.getTimeOfGame();
    }

    @Override
    public void run() {
        this.timeLeft--;
        if (this.timeLeft <= 0){
            cancel();
            gameInstance.prepareToEnd();
        }
    }

}
