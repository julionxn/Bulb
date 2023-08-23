package net.pulga22.bulb.core.worlds;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * <p>This class is an intermediary between the game instance and the physical world.</p>
 * It contains auxiliary methods to teleport players and get locations within this world.
 */
public class WorldInstance {

    private final Plugin plugin;
    private final int gameId;
    private final WorldOption worldOption;
    private final LoadedWorld loadedWorld;
    private volatile boolean loaded = false;

    public WorldInstance(Plugin plugin, int gameId, WorldOption worldOption){
        this.plugin = plugin;
        this.gameId = gameId;
        this.worldOption = worldOption;
        this.loadedWorld = new LoadedWorld(this.plugin, this.worldOption, this.gameId);
    }

    /**
     * Starts the game world instance.
     */
    public void start(Runnable onReady){
        if (this.loaded) return;
        this.plugin.getLogger().info(">Creating a new world instance for game with id " + this.gameId + ".");
        this.loadedWorld.load(onReady);
        this.loaded = true;
    }

    /**
     * @return If the world is already loaded and operative.
     */
    public boolean isLoaded(){
        return this.loaded && this.loadedWorld.isLoaded();
    }

    /**
     * Finishes the game world instance.
     */
    public void finish(){
        if (!this.loaded) return;
        this.loadedWorld.unload();
        this.loaded = false;
    }

    /**
     * Teleports the player to default spawn-point location of the game world instance.
     * @param player The player.
     */
    public void teleportPlayer(Player player){
        if (!this.loaded) return;
        this.teleportPlayer(player, this.loadedWorld.getSpawnLocation());
    }

    /**
     * Teleports the player to a specific location inside the game world instance.
     * @param player The player.
     * @param x X coordinate of the world.
     * @param y Y coordinate of the world.
     * @param z Z coordinate of the world.
     */
    public void teleportPlayer(Player player, double x, double y, double z){
        if (!this.loaded) return;
        Location location = new Location(this.loadedWorld.getWorld(), x, y, z);
        this.teleportPlayer(player, location);
    }

    /**
     * Teleports the player to a specific location inside the game world instance.
     * @param player The player.
     * @param x X coordinate of the world.
     * @param y Y coordinate of the world.
     * @param z Z coordinate of the world.
     * @param yaw Starting yaw of the player. (Horizontal Axis)
     * @param pitch Starting pitch of the player. (Vertical Axis)
     */
    public void teleportPlayer(Player player, double x, double y, double z, float yaw, float pitch){
        if (!this.loaded) return;
        Location location = new Location(this.loadedWorld.getWorld(), x, y, z, yaw, pitch);
        this.teleportPlayer(player, location);
    }

    private void teleportPlayer(Player player, Location location){
        this.loadedWorld.teleportPlayerTo(player, location);
    }

    /**
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return The according Location of the coords.
     */
    public Location getLocation(double x, double y, double z){
        return new Location(getWorld(), x, y, z);
    }

    /**
     * @param poi The Point of Interest.
     * @return The according location of the poi.
     */
    public Location getLocationOfPoi(PointOfInterest poi){
        return new Location(this.getWorld(), poi.x(), poi.y(), poi.z(), poi.yaw(), poi.pitch());
    }

    /**
     * @param key The key of the Point of Interest
     * @return The according Location of that poi,
     */
    public Location getLocationOfPoi(String key){
        PointOfInterest poi = this.worldOption.getPointOfInterest(key);
        return this.getLocationOfPoi(poi);
    }

    /**
     * @return The worldOption of this world instance.
     */
    public WorldOption getWorldOption(){
        return this.worldOption;
    }

    public String getVanillaWorldPath(){
        return this.loadedWorld.getVanillaWorldPath();
    }

    /**
     * @return The CraftBukkit World of the game.
     */
    public World getWorld(){
        return this.loadedWorld.getWorld();
    }

}
