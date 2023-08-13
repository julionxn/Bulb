package net.pulga22.myplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerTracker extends BukkitRunnable {

    private final MyMinigame gameInstance;

    public PlayerTracker(MyMinigame gameInstance) {
        this.gameInstance = gameInstance;
    }

    @Override
    public void run() {
        this.gameInstance.getPlayerManager().getPlayersPlaying().forEach(player -> {
            Location location = player.getLocation();
            if (location.getY() <= -64){
                this.gameInstance.markPlayerAsDead(player);
                if (gameInstance.alivePlayers.get() <= 1){
                    gameInstance.prepareToEnd();
                }
                return;
            }
            location.getWorld().getBlockAt(location.add(0, -0.1, 0)).setType(Material.AIR);
        });
    }

}
