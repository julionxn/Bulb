package net.pulga22.bulb.util;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginUtils {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

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
