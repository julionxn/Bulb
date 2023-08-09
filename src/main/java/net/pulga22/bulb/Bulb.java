package net.pulga22.bulb;

import net.pulga22.bulb.util.PluginUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Bulb extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = this.getLogger();
        logger.info(PluginUtils.ANSI_GREEN + "=============================================");
        logger.info("");
        logger.info("=============================================" + PluginUtils.ANSI_RESET);
    }

}
