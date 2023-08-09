package net.pulga22.minigamecore.core.functional;

import net.pulga22.minigamecore.core.GameInstance;
import net.pulga22.minigamecore.core.GameManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@FunctionalInterface
public interface IGameInstanceListener<T extends GameInstanceListener<K,P>, K extends GameInstance<P>, P extends Plugin> extends Listener {
    T create(GameManager<K, P> gameManager);
}
