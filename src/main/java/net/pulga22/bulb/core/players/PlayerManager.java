package net.pulga22.bulb.core.players;

import net.pulga22.bulb.core.states.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

/**
 * <p>This class is responsible for managing the players of a game instance.<br>
 * This includes:</p>
 * <li>Managing who is a spectator and who is a player.</li>
 * <li>Which team each player belongs to</li>
 * @param <T> extends Plugin.
 */
public class PlayerManager<T extends Plugin> {

    private static final Random RANDOM = new Random();
    private final PlayersContainer playersContainer = new PlayersContainer();
    private final int maxPlayers;

    public PlayerManager(int maxPlayers){
        this.maxPlayers = maxPlayers;
    }

    /**
     * Joins the player to the game, if the game is full, joins the player as spectator.
     * @param player The player who wants to join.
     * @return If the player has been joined as spectator or as a player.
     */
    @Nullable
    public PlayerState joinPlayer(Player player){
        if (this.playersContainer.isSpectating(player) || this.playersContainer.isPlaying(player)) return null;
        if (this.playersContainer.getPlaying().size() > getMaxPlayersPerGame()){
            joinPlayerAsSpectator(player);
            return PlayerState.SPECTATING;
        }
        this.playersContainer.joinPlayer(player);
        return PlayerState.PLAYING;
    }

    public void joinPlayerAsSpectator(Player player){
        this.playersContainer.joinSpectator(player);
    }

    /**
     * @param player The player who has been playing.
     */
    public void playerToSpectator(Player player){
        if (!this.playersContainer.isPlaying(player)) return;
        this.playersContainer.quitPlayer(player);
        this.playersContainer.joinSpectator(player);
    }

    /**
     * @param player The player who has been spectating.
     */
    public void spectatorToPlayer(Player player){
        if (!this.playersContainer.isSpectating(player)) return;
        this.playersContainer.quitSpectator(player);
        this.playersContainer.joinPlayer(player);
    }

    /**
     * @return If the game is full.
     */
    public boolean isGameFull(){
        return this.playersContainer.getPlaying().size() > getMaxPlayersPerGame();
    }

    /**
     * @return Max players per game.
     */
    public int getMaxPlayersPerGame(){
        return this.maxPlayers;
    }

    /**
     * @param player The player to check.
     * @return The PlayerState according if the player is playing or spectating.
     */
    @Nullable
    public PlayerState getPlayerState(Player player){
        if (getPlayersPlaying().contains(player)) return PlayerState.PLAYING;
        if (getPlayersSpectating().contains(player)) return PlayerState.SPECTATING;
        return null;
    }

    public List<Player> getPlayersPlaying(){
        return this.playersContainer.getPlaying();
    }

    public List<Player> getPlayersSpectating(){
        return this.playersContainer.getSpectators();
    }

}
