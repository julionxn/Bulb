package net.pulga22.minigamecore.core.functional;

import net.pulga22.minigamecore.core.GameInstance;
import net.pulga22.minigamecore.core.GameManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * This is like a "shortcut" the get easy access to the {@link GameManager} inside a Listener. <br>
 * The subclasses must be registered in the GameManager object.
 * @param <T> The game instance class of the manager.
 * @param <K> The plugin class of the manager.
 * @see GameManager#registerListeners(IGameInstanceListener[])
 */
public class GameInstanceListener<T extends GameInstance<K>, K extends Plugin> implements Listener {

    protected final GameManager<T, K> gameManager;

    public GameInstanceListener(GameManager<T, K> gameManager) {
        this.gameManager = gameManager;
    }

}
