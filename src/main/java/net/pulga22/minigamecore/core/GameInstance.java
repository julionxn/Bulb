package net.pulga22.minigamecore.core;

import net.pulga22.minigamecore.core.config.ConfigManager;
import net.pulga22.minigamecore.core.players.PlayerManager;
import net.pulga22.minigamecore.core.runnables.CountdownGameEndRunnable;
import net.pulga22.minigamecore.core.runnables.CountdownGameStartRunnable;
import net.pulga22.minigamecore.core.runnables.TimerRunnable;
import net.pulga22.minigamecore.core.score.GameScoreboard;
import net.pulga22.minigamecore.core.score.GameScoreboardInfo;
import net.pulga22.minigamecore.core.states.GameState;
import net.pulga22.minigamecore.core.states.PlayerState;
import net.pulga22.minigamecore.core.teams.TeamInfo;
import net.pulga22.minigamecore.core.worlds.WorldInstance;
import net.pulga22.minigamecore.core.worlds.WorldOption;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <div><p><b>This is the main class of the whole Plugin.</b><br>
 * All the other classes are focused on satisfying the needs of this one.<br>
 * This class contains all the infrastructure and base logic for a game.<br>
 * This class does not contain any game mechanic, that's onto you.<br><br>
 * Certain methods <b>MUST</b> be overridden these are called to get basic
 * parameters about the game:</p>
 * <li>
 *     <b>{@link #getMaxPlayers()}</b> the return value determines the maximum amount of players inside the game.
 * </li>
 * <li>
 *     <b>{@link #getTimeBeforeStart()}</b> when the game is prepared, determines the amount of seconds before the game starts.
 * </li>
 * <li>
 *     <b>{@link #getTimeOfGame()}</b> when the game has started, determines the amount of seconds before the game prepares to end.
 * </li>
 * <li>
 *     <b>{@link #getTimeBeforeEnd()}</b> when the game is prepared to end, determines the amount of seconds before the game ends.
 * </li>
 * <br></div>
 * <div><p>Other methods do not need to be overwritten, however, it is possible to do so to determine other aspects about the game instance.</p>
 * <li>
 *     <b>{@link #getTeamsInfo(TeamInfo)}</b> It is used to determine certain parameters when establishing the teams of the players entering the game instance.
 * </li>
 * <li>
 *     <b>{@link #initScoreboard(GameScoreboard)}</b> It is used to create and initialize components inside the scoreboard of the game instance.
 * </li><br>
 * </div>
 * <p>It has several methods that are <b>recommended</b> to be overridden to perform actions on certain events of the game instance:</p>
 * <li>
 *     <b>{@link #onPreparation()}</b> triggers when a game is prepared. <br>
 *     A game instance can be created but no prepared.<br>
 *     Players are only allowed to enter the game instance when it is prepared.<br>
 *     When the instance is prepared, it automatically starts a counter parameterized
 *     by {@link #getTimeBeforeStart()} which, at the end, starts the game.<br>
 *     By default the {@link GameManager} automatically prepares the game instance.
 * </li>
 * <li>
 *     <b>{@link #onJoinedCommon(Player)}</b> triggers when a player enters the game.<br>
 *     Whether the player enters as a player or as a spectator. <br>
 *     The deciding factor for how to enter is given by the maximum number of players allowed.<br>
 *     See: {@link #getMaxPlayers()}
 * </li>
 * <li>
 *     <b>{@link #onJoinedAsPlayer(Player)}</b> triggers only when a player is joined as a player.
 * </li>
 * <li>
 *     <b>{@link #onJoinedAsSpectator(Player)}</b>  triggers only when a player is joined as a spectator.<br>
 *     By default it hides the player from everyone else inside.
 * </li>
 * <li>
 *     <b>{@link #onGameStarted(List, List)}</b> triggers when the game instance is started.<br>
 *     When the instance is started, it automatically starts a counter parameterized
 *     by {@link #getTimeOfGame()} which, at the end, prepares to end the game.<br>
 * </li>
 * <li>
 *     <b>{@link #onDeath(World, Player)}</b> triggers when a player is marked as death.<br>
 *     See: {@link #markPlayerAsDead(Player)}
 * </li>
 * <li>
 *     <b>{@link #onPrepareToEnd(List, List)}</b> triggers when the game instance is prepared to end.<br>
 *     When the instance is prepared to end, it automatically starts a counter parameterized
 *     by {@link #getTimeBeforeEnd()} which, at the end, completely ends the game.<br>
 * </li>
 * <li>
 *     <b>{@link #onEnding(List, List)}</b> triggers when the game instance is completely ended.<br>
 * </li>
 * <li>
 *     <b>{@link #onGameStateChange(GameState, GameState)}</b> triggers when the Game State of the game instance<br>
 *     is changed.
 * </li>
 * @param <T> extends Plugin.
 */
public class GameInstance<T extends Plugin> {

    protected final T plugin;
    protected final Logger logger;
    private final GameManager<? extends GameInstance<T>, T> gameManager;
    private GameState gameState = GameState.NONE;
    private boolean prepared = false;
    protected final PlayerManager<T> playerManager;
    private final GameScoreboard scoreboard;
    private final int ID;
    public final String gameName;
    private final WorldInstance worldInstance;
    private final HashSet<BukkitRunnable> timersRunnable = new HashSet<>();
    private final List<Player> queueToJoin = new ArrayList<>();

    public <K extends GameInstance<T>> GameInstance(T plugin, ConfigManager<T> configManager, GameManager<K, T> manager, String gameName, WorldOption worldOption, GameScoreboardInfo info, boolean prepareOnCreation) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.gameManager = manager;
        this.gameName = gameName;
        this.ID = manager.random.nextInt(Integer.MAX_VALUE);
        TeamInfo<T> teamInfo = this.getTeamsInfo(new TeamInfo<>(configManager));
        this.playerManager = new PlayerManager<>(getMaxPlayers(), teamInfo, configManager);
        if (!info.empty()){
            this.scoreboard = new GameScoreboard(info, this.gameName);
            this.initScoreboard(this.scoreboard);
        } else {
            this.scoreboard = null;
        }
        this.worldInstance = new WorldInstance(plugin, this.ID, worldOption);
        this.logger.info(">New game instance of " + this.gameName + " with id " + this.ID + " created.");
        if (prepareOnCreation) this.prepare();
    }

    public int getID() {
        return this.ID;
    }

    /**
     * <p><b>MUST OVERRIDE.</b></p>
     * @return Max players per game.
     * {@link }
     */
    @MustOverride("Subclasses must override this method.")
    public int getMaxPlayers(){
        return 4;
    }

    /**
     * <p><b>MUST OVERRIDE.</b></p>
     * @return The time before the match starts after being prepared.
     */
    @MustOverride("Subclasses must override this method.")
    public int getTimeBeforeStart(){
        return 30;
    }

    /**
     * <p><b>MUST OVERRIDE.</b></p>
     * @return The time the match lasts.
     */
    @MustOverride("Subclasses must override this method.")
    public int getTimeOfGame(){
        return 60;
    }

    /**
     * <p><b>MUST OVERRIDE.</b></p>
     * @return The time before the match ends after finishing.
     */
    @MustOverride("Subclasses must override this method.")
    public int getTimeBeforeEnd(){
        return 30;
    }

    /**
     * Used to set the teams info about the game. Return null to not use teams in this game.
     * <p><b>Intended to override.</b></p>
     * @param teamInfo The teams' info.
     */
    public TeamInfo<T> getTeamsInfo(TeamInfo<T> teamInfo){
        return null;
    }

    /**
     * Used to initialize the components of the Greeko Scoreboard.
     * <p><b>Intended to override.</b></p>
     * @param gameScoreboard The Greeko Scoreboard of the game instance.
     */
    public void initScoreboard(GameScoreboard gameScoreboard){

    }

    /**
     * Prepares the game. Creates the world, and starts the countdown.
     * @see GameInstance#onPreparation()
     */
    public final void prepare(){
        this.worldInstance.start(this::prepareWhenWorldInstanceReady);
    }

    private synchronized void prepareWhenWorldInstanceReady(){
        if (this.gameState == GameState.PREPARED) return;
        this.changeGameState(GameState.PREPARED);
        CountdownGameStartRunnable<T> countdownGameStart = new CountdownGameStartRunnable<>(this);
        countdownGameStart.runTaskTimer(this.plugin, 0, 20);
        this.timersRunnable.add(countdownGameStart);
        this.prepared = true;
        this.queueToJoin.forEach(this::joinPlayer);
        this.queueToJoin.clear();
        onPreparation();
    }

    /**
     * Triggers when the game has been prepared.
     * <p><b>Intended to override.</b></p>
     */
    protected void onPreparation(){

    }

    /**
     * <p>The game needs to be prepared to be able to join players.</p>
     * Joins the player into the game. If the game have started or the game is full, joins the player as spectator.
     * @param player The player who wants to join.
     * @return The PlayerState of the joined player.
     */
    public final PlayerState joinPlayer(Player player){
        if (!this.prepared) {
            this.queueToJoin.add(player);
            return PlayerState.PLAYING;
        }
        if (this.gameState == GameState.NONE){
            this.logger.log(Level.SEVERE, "Game must be prepared first after try to join players.");
            return null;
        }
        if (this.scoreboard != null){
            this.scoreboard.showToPlayer(player);
        }
        if (this.gameState == GameState.PLAYING){
            this.playerManager.joinPlayerAsSpectator(player);
            onJoinedCommon(player);
            onJoinedAsSpectator(player);
            return PlayerState.SPECTATING;
        }
        final PlayerState playerState = this.playerManager.joinPlayer(player);
        onJoinedCommon(player);
        if (playerState == PlayerState.PLAYING){
            onJoinedAsPlayer(player);
        } else if (playerState == PlayerState.SPECTATING) {
            onJoinedAsSpectator(player);
        }
        return playerState;
    }

    /**
     * Joins a player with a specific team.
     * @param player The player.
     * @param team The team key.
     * @return The PlayerState.
     */
    public final PlayerState joinPlayerToTeam(Player player, String team){
        this.playerManager.joinPlayerToTeam(player, team);
        return this.joinPlayer(player);
    }

    /**
     * Always triggers on the player who joined the game.
     * <p><b>Intended to override.</b></p>
     * @see WorldInstance#joinPlayer(Player, double, double, double)
     * @see WorldInstance#joinPlayer(Player, double, double, double, float, float)
     * @param player The player who joined.
     */
    protected void onJoinedCommon(Player player){
        this.worldInstance.joinPlayer(player);
    }

    /**
     * Triggers on the player if joins as a player.
     * <p><b>Intended to override.</b></p>
     * @see WorldInstance#joinPlayer(Player, double, double, double)
     * @see WorldInstance#joinPlayer(Player, double, double, double, float, float)
     * @param player The player who joined.
     */
    protected void onJoinedAsPlayer(Player player){
    }

    /**
     * Triggers on the player if joins as a spectator.
     * <p><b>Intended to override.</b></p>
     * @see WorldInstance#joinPlayer(Player, double, double, double)
     * @see WorldInstance#joinPlayer(Player, double, double, double, float, float)
     * @param player The player who joined.
     */
    protected void onJoinedAsSpectator(Player player){
        hidePlayer(player);
    }

    /**
     * <p>Starts the game.</p>
     * <b>Not intended to use directly,</b> since it is called after the countdown ends triggered
     * in the {@link #prepare()} method.
     * @see GameInstance#onGameStarted(List, List)
     */
    public final void startGame(){
        this.changeGameState(GameState.PLAYING);
        TimerRunnable<T> countdownGameStart = new TimerRunnable<>(this);
        countdownGameStart.runTaskTimer(this.plugin, 0, 20);
        this.timersRunnable.add(countdownGameStart);
        onGameStarted(this.playerManager.getPlayersPlaying(), this.playerManager.getPlayersSpectating());
    }

    /**
     * Triggers when the game has been started.
     * <p><b>Intended to override.</b></p>
     * @param players All the players inside the game.
     * @param spectators All the spectators inside the game.
     */
    protected void onGameStarted(List<Player> players, List<Player> spectators){

    }

    /**
     * Changes the player to spectator and triggers {@link #onDeath(World, Player)}<br>
     * You should create your own logic to trigger this function.
     * @param player The player marked as dead.
     * @see GameInstance#onDeath(World, Player)
     */
    public final void markPlayerAsDead(Player player){
        this.playerManager.playerToSpectator(player);
        onDeath(this.worldInstance.getWorld(), player);
    }

    /**
     * Triggers when a player is marked as dead. By default, just hides him.
     * <p><b>Intended to override.</b></p>
     * @param player The player who has died.
     */
    protected void onDeath(World world, Player player){
        hidePlayer(player);
    }

    /**
     * @return The amount of alive players.
     */
    public int getAlivePlayers(){
        return this.playerManager.getPlayersPlaying().size();
    }

    public boolean isFull(){
        return this.playerManager.isGameFull() || this.queueToJoin.size() > this.getMaxPlayers();
    }

    /**
     * Prepares to end the game. Starts the countdown to end it.
     * @see GameInstance#onPrepareToEnd(List, List)
     */
    public final void prepareToEnd(){
        changeGameState(GameState.FINISHING);
        CountdownGameEndRunnable<T> countdownGameEnd = new CountdownGameEndRunnable<>(this);
        countdownGameEnd.runTaskTimer(this.plugin, 0, 20);
        this.timersRunnable.add(countdownGameEnd);
        onPrepareToEnd(this.playerManager.getPlayersPlaying(), this.playerManager.getPlayersSpectating());
    }

    /**
     * Triggers when the game is preparing to end.
     * <p><b>Intended to override.</b></p>
     * @param players All the players inside the game.
     * @param spectators All the spectators inside the game.
     */
    protected void onPrepareToEnd(List<Player> players, List<Player> spectators){

    }

    /**
     * <p>Ends the game.</p>
     * <b>Not intended to use directly,</b> since it is called after the countdown ends triggered
     * in the {@link #prepareToEnd()} method.
     * @see GameInstance#onEnding(List, List)
     */
    public final void endGame(){
        changeGameState(GameState.FINISHED);
        World mainWorld = Bukkit.getServer().getWorlds().get(0);
        this.playerManager.getPlayersPlaying().forEach(player -> player.teleport(mainWorld.getSpawnLocation()));
        this.playerManager.getPlayersSpectating().forEach(player -> {
            this.showPlayer(player);
            player.teleport(mainWorld.getSpawnLocation());
        });
        this.worldInstance.finish();
        this.gameManager.finishGame(this);
        if (this.scoreboard != null){
            this.scoreboard.delete();
        }
        this.timersRunnable.forEach(BukkitRunnable::cancel);
        onEnding(this.playerManager.getPlayersPlaying(), this.playerManager.getPlayersSpectating());
    }

    /**
     * Triggers when the game is preparing to end.
     * <p><b>Intended to override.</b></p>
     * @param players All the players inside the game.
     * @param spectators All the spectators inside the game.
     */
    protected void onEnding(List<Player> players, List<Player> spectators){

    }

    /**
     * Hides a player to everyone inside the game.
     * @param player The player to hide.
     */
    public final void hidePlayer(Player player){
        this.playerManager.getPlayersPlaying().forEach(player1 -> player1.hidePlayer(this.plugin, player));
        this.playerManager.getPlayersSpectating().forEach(player1 -> player1.hidePlayer(this.plugin, player));
    }

    /**
     * Shows a player to everyone inside the game.
     * @param player The player to show.
     */
    public final void showPlayer(Player player){
        this.playerManager.getPlayersPlaying().forEach(player1 -> player1.showPlayer(this.plugin, player));
        this.playerManager.getPlayersSpectating().forEach(player1 -> player1.showPlayer(this.plugin, player));
    }

    /**
     * @return The current GameState of the game.
     */
    public GameState getGameState(){
        return this.gameState;
    }

    private void changeGameState(GameState gameState){
        if (this.gameState == gameState) return;
        this.onGameStateChange(this.gameState, gameState);
        this.gameState = gameState;
    }

    /**
     * Triggers when the GameState is updated.
     * <p><b>Intended to override.</b></p>
     * @param previousState Previous GameState.
     * @param newState The new GameState.
     */
    protected void onGameStateChange(GameState previousState, GameState newState){

    }

    /**
     * @return The WorldOption containing all the info about the chosen world of the game.
     */
    public WorldInstance getWorldInstance(){
        return this.worldInstance;
    }

    /**
     * @return The PlayerManager of the game.
     */
    public PlayerManager<T> getPlayerManager(){
        return this.playerManager;
    }

}