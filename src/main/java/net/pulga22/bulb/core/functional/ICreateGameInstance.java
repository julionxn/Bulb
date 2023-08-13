package net.pulga22.bulb.core.functional;

import net.pulga22.bulb.core.GameInstance;
import net.pulga22.bulb.core.GameManager;
import net.pulga22.bulb.core.config.ConfigManager;
import net.pulga22.bulb.core.score.GameScoreboardInfo;
import net.pulga22.bulb.core.worlds.WorldOption;
import org.bukkit.plugin.Plugin;

@FunctionalInterface
public interface ICreateGameInstance<T extends GameInstance<K>, K extends Plugin> {
    T create(K plugin, GameManager<T, K> gameManager, String gameName, WorldOption worldOption, boolean prepareOnCreation);
}
