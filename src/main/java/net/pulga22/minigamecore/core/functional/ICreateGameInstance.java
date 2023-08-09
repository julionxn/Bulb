package net.pulga22.minigamecore.core.functional;

import net.pulga22.minigamecore.core.GameInstance;
import net.pulga22.minigamecore.core.GameManager;
import net.pulga22.minigamecore.core.config.ConfigManager;
import net.pulga22.minigamecore.core.score.GameScoreboardInfo;
import net.pulga22.minigamecore.core.worlds.WorldOption;
import org.bukkit.plugin.Plugin;

@FunctionalInterface
public interface ICreateGameInstance<T extends GameInstance<K>, K extends Plugin> {
    T create(K plugin, ConfigManager<K> configManager, GameManager<T, K> gameManager, String gameName, WorldOption worldOption, GameScoreboardInfo info, boolean prepareOnCreation);
}
