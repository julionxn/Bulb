package net.pulga22.bulb.core;

import net.pulga22.bulb.core.config.ConfigManager;
import net.pulga22.bulb.core.functional.GameInstanceListener;
import net.pulga22.bulb.core.functional.ICreateGameInstance;
import net.pulga22.bulb.core.functional.IGameInstanceListener;
import net.pulga22.bulb.core.score.GameScoreboardInfo;
import net.pulga22.bulb.core.states.PlayerState;
import net.pulga22.bulb.core.worlds.WorldOption;
import net.pulga22.bulb.util.PluginUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * This class is responsible for creating new game instances when necessary, keeping track of the instances created, and keeping track of the players within the instances.
 * @param <T> The game instance class which are going to be managed.
 * @param <K> extends Plugin.
 */
public class GameManager<T extends GameInstance<K>, K extends Plugin> {

    protected final Random random = new Random();
    protected final K plugin;
    protected final String gameName;
    protected T availableGame;
    protected int availableGameId;
    protected final HashMap<Integer, T> games = new HashMap<>();
    protected final HashMap<String, Integer> playing = new HashMap<>();
    protected final HashMap<String, Integer> spectating = new HashMap<>();
    protected final ICreateGameInstance<T, K> runnable;
    protected final HashSet<WorldOption> worldOptions;
    protected final ConfigManager<K> configManager;

    protected GameManager(K plugin, ConfigManager<K> configManager, String gameName, ICreateGameInstance<T, K> gameInstance) {
        this.plugin = plugin;
        this.gameName = gameName;
        this.runnable = gameInstance;
        this.configManager = configManager;
        this.worldOptions = configManager.getWorldsOfGame(gameName);
    }

    public static <G extends GameInstance<P>, P extends Plugin> GameManager<G, P> register(@NotNull P plugin, @NotNull ConfigManager<P> configManager, @NotNull String gameName, @NotNull ICreateGameInstance<G, P> gameInstance){
        HashSet<WorldOption> worlds = configManager.getWorldsOfGame(gameName);
        if (worlds == null || worlds.isEmpty()){
            plugin.getLogger().severe("No worlds of " + gameName + " founded.");
        }
        return new GameManager<>(plugin, configManager, gameName, gameInstance);
    }

    /**
     * @param listeners Listeners to register.
     */
    @SafeVarargs
    public final <L extends GameInstanceListener<T, K>> void registerListeners(IGameInstanceListener<L, T, K>... listeners){
        for (IGameInstanceListener<L, T, K> listener : listeners) {
            this.plugin.getServer().getPluginManager().registerEvents(listener.create(this), this.plugin);
        }
    }

    /**
     * Joins a player to a game.
     * @param player Player who wants to join.
     */
    public void joinPlayer(Player player){
        if (this.isInsideGame(player)) return;
        this.shouldInitNewGame();
        PlayerState playerState = this.availableGame.joinPlayer(player);
        this.registerPlayer(player, playerState);
    }

    private void shouldInitNewGame(){
        if (this.availableGame == null || this.availableGame.isFull()){
            T game  = this.newGame();
            this.availableGame = game;
            this.availableGameId = game.getID();
            this.games.put(this.availableGameId, this.availableGame);
        }
    }

    private void registerPlayer(Player player, PlayerState playerState){
        String uuid = PluginUtils.uuidOf(player);
        if (playerState == PlayerState.PLAYING){
            this.playing.put(uuid, this.availableGameId);
        } else if (playerState == PlayerState.SPECTATING) {
            this.spectating.put(uuid, this.availableGameId);
        }
    }

    /**
     * @return Return a new Game Instance.
     */
    protected T newGame(){
        WorldOption option = getRandomWorldOption();
        return runnable.create(this.plugin, this, this.gameName, option, true);
    }

    private WorldOption getRandomWorldOption(){
        WorldOption[] values = this.worldOptions.toArray(new WorldOption[0]);
        return values[random.nextInt(values.length)];
    }

    /**
     * @param game The Game Instance.
     * @param <P> Plugin.
     */
    protected <P extends Plugin> void finishGame(GameInstance<P> game){
        if (game.getID() == this.availableGame.getID()){
            this.availableGame = null;
        }
        this.games.remove(game.getID());
        game.getPlayerManager().getPlayersPlaying().forEach(player -> {
            String uuid = PluginUtils.uuidOf(player);
            this.playing.remove(uuid);
        });
        game.getPlayerManager().getPlayersSpectating().forEach(player -> {
            String uuid = PluginUtils.uuidOf(player);
            this.spectating.remove(uuid);
            this.playing.remove(uuid);
        });
        this.plugin.getLogger().info(">Ended game with id " + game.getID() + ".");
    }

    /**
     * @return Get all active games.
     */
    public HashMap<Integer, T> getGames(){
        return this.games;
    }

    /**
     * Ends all active games.
     */
    public void endGames(){
        getGames().forEach((id, game) -> game.endGame());
    }

    /**
     * @param player The player to get its game.
     * @return The game.
     */
    public T getGameOfPlayer(Player player){
        String uuid = PluginUtils.uuidOf(player);
        int gameId = this.playing.get(uuid);
        return this.games.get(gameId);
    }

    /**
     * @param player The player to get its game.
     * @return The game.
     */
    public T getGameOfSpectator(Player player){
        String uuid = PluginUtils.uuidOf(player);
        int gameId = this.spectating.get(uuid);
        return this.games.get(gameId);
    }

    public boolean isPlaying(Player player){
        String uuid = PluginUtils.uuidOf(player);
        return this.playing.containsKey(uuid);
    }

    public boolean isSpectating(Player player){
        String uuid = PluginUtils.uuidOf(player);
        return this.spectating.containsKey(uuid);
    }

    public boolean isInsideGame(Player player){
        return this.isSpectating(player) || this.isPlaying(player);
    }

}
