package net.pulga22.bulb.core.worlds;

import net.pulga22.bulb.core.worlds.threads.CloneWorldTask;
import net.pulga22.bulb.core.worlds.threads.DeleteWorkTask;
import net.pulga22.bulb.core.worlds.threads.WorldsIOThreadManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * This class represents a physical world inside the server.
 */
public class LoadedWorld {

    private final Plugin plugin;
    private final Logger logger;
    private final String newWorldName;
    private final Path templatePath;
    private final Path newWorldPath;
    private final String vanillaNewWorldPath;
    private World world;
    protected volatile boolean loaded = false;

    public LoadedWorld(Plugin plugin, WorldOption worldOption, int gameId) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.templatePath = worldOption.getWorldTemplatePath();
        this.newWorldName = String.format("temp_%s_%d", worldOption.getWorldName(), gameId);
        this.newWorldPath = worldOption.getGamesPath().resolve(newWorldName);
        this.vanillaNewWorldPath = "plugins/" + this.plugin.getName() + "/games/" + this.newWorldName;
    }

    /**
     * Clones the world in another thread, after it is cloned, loads the world into Bukkit.
     * @param onReady Actions to perform when the world is loaded.
     */
    public synchronized void load(Runnable onReady){
        if (this.loaded) return;
        logger.info(">Trying to load the world " + this.newWorldName + ".");
        WorldsIOThreadManager.getInstance().runTask(
                CloneWorldTask.of(this.plugin, this.templatePath, this.newWorldPath, () -> this.loadWorldToBukkit(onReady))
        );
    }

    private synchronized void loadWorldToBukkit(Runnable onReady){
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            if (this.loaded) return;
            this.world = Bukkit.createWorld(new WorldCreator(this.vanillaNewWorldPath));
            if (this.world != null){
                this.world.setAutoSave(false);
            }
            this.loaded = true;
            logger.info(">World " + this.newWorldName + " loaded.");
            onReady.run();
        });
    }

    /**
     * Unloads the world form bukkit, then in another thread, delete the world files.
     */
    public void unload(){
        Bukkit.unloadWorld(this.world, false);
        logger.info(">World " + this.newWorldName + " unloaded on the server.");
        this.world = null;
        this.loaded = false;
        WorldsIOThreadManager.getInstance().runTask(
                DeleteWorkTask.of(this.plugin, this.newWorldPath, () -> logger.info(">World " + this.newWorldName + " removed."))
        );
    }

    /**
     * @return If the world has already been uploaded to Bukkit
     */
    public boolean isLoaded(){
        return this.loaded;
    }

    public Location getSpawnLocation(){
        return this.world.getSpawnLocation();
    }

    public void teleportPlayerTo(Player player, Location location){
        player.teleport(location);
    }

    /**
     * @return The Bukkit World.
     */
    public World getWorld(){
        return this.world;
    }

    public String getVanillaWorldPath(){
        return this.vanillaNewWorldPath;
    }

}
