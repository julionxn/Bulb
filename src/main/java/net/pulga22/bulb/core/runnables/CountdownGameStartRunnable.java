package net.pulga22.bulb.core.runnables;

import net.pulga22.bulb.core.GameInstance;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownGameStartRunnable<T extends Plugin> extends BukkitRunnable {

    private final GameInstance<T> gameInstance;
    private int timeLeft;

    public CountdownGameStartRunnable(GameInstance<T> gameInstance) {
        this.gameInstance = gameInstance;
        this.timeLeft = this.gameInstance.getTimeBeforeStart();
    }

    @Override
    public void run() {
        this.timeLeft--;
        if (this.timeLeft <= 0){
            cancel();
            gameInstance.startGame();
        }
    }
}
