package net.pulga22.minigamecore.util;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginUtils {

    public static void registerListener(Plugin plugin, Listener listener){
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static void registerCommand(JavaPlugin plugin, String command, CommandExecutor executor){
        PluginCommand pluginCommand = plugin.getCommand(command);
        if (pluginCommand == null) return;
        pluginCommand.setExecutor(executor);
    }

    public static String uuidOf(Player player){
        return player.getUniqueId().toString();
    }

}
