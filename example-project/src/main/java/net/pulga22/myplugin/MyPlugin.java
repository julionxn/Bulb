package net.pulga22.myplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.pulga22.bulb.core.GameManager;
import net.pulga22.bulb.core.config.ConfigManager;
import net.pulga22.bulb.core.score.GameScoreboardInfo;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin {

    private GameManager<MyMinigame, MyPlugin> tntManager;

    @Override
    public void onEnable() {
        //Register our config
        ConfigManager<MyPlugin> myConfig = ConfigManager.register(this);
        //The scoreboard info that is going to be used in the minigame
        GameScoreboardInfo gameScoreboardInfo = new GameScoreboardInfo(
                Component.text("[MyServer] " , TextColor.color(0x00ff00)),
                0xff0000,
                "myserver.net");
        this.tntManager = GameManager.register(
                this,
                myConfig,
                "tntrun",
                MyMinigame::new,
                gameScoreboardInfo
        );
        this.tntManager.registerListeners(
                OnJoinListener::new
        );
    }
    @Override
    public void onDisable() {
       this.tntManager.endGames();
    }
}
