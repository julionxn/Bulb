package net.pulga22.bulb.core.runnables;

import net.pulga22.bulb.core.GameInstance;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CountdownGameEndRunnable<T extends Plugin> extends BukkitRunnable {

    private final GameInstance<T> gameInstance;
    private int timeLeft;

    public CountdownGameEndRunnable(GameInstance<T> gameInstance) {
        this.gameInstance = gameInstance;
        this.timeLeft = this.gameInstance.getTimeBeforeEnd();
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
            gameInstance.endGame();
        }
    }
}
