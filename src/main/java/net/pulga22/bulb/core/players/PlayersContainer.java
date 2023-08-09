package net.pulga22.bulb.core.players;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This auxiliary class is just a container that handles players and spectators.
 */
public class PlayersContainer {

    private final List<Player> playing = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();

    public PlayersContainer(){

    }

    public void joinPlayer(Player player){
        if (this.isPlaying(player)) return;
        this.playing.add(player);
    }

    public void quitPlayer(Player player){
        if (!this.isPlaying(player)) return;
        this.playing.remove(player);
    }

    public void joinSpectator(Player player){
        if (this.isPlaying(player)){
            quitPlayer(player);
        }
        if (this.isSpectating(player)) return;
        this.spectators.add(player);
    }

    public void quitSpectator(Player player){
        if (!this.isSpectating(player)) return;
        this.spectators.remove(player);
    }

    public List<Player> getPlaying(){
        return this.playing;
    }

    public List<Player> getSpectators(){
        return this.spectators;
    }

    public boolean isPlaying(Player player){
        return this.playing.contains(player);
    }

    public boolean isSpectating(Player player){
        return this.spectators.contains(player);
    }

}
