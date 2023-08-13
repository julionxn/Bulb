package net.pulga22.myplugin;

import net.pulga22.bulb.core.GameManager;
import net.pulga22.bulb.core.functional.GameInstanceListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinListener extends GameInstanceListener<MyMinigame, MyPlugin> {

    public OnJoinListener(GameManager<MyMinigame, MyPlugin> gameManager) {
        super(gameManager);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        this.gameManager.joinPlayer(player);
    }

}
