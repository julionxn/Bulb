package net.pulga22.bulb.core.players;

import net.pulga22.bulb.core.config.ConfigManager;
import net.pulga22.bulb.core.states.PlayerState;
import net.pulga22.bulb.core.teams.CustomTeam;
import net.pulga22.bulb.core.teams.TeamsInfo;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
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
    private final TeamsInfo<T> teamsInfo;
    private boolean teamsGame;
    private final HashMap<String, CustomTeam> allowedTeams = new HashMap<>();
    private final HashMap<Player, String> teams = new HashMap<>();
    private final List<String> uniformTeamsList = new ArrayList<>();
    private int uniformIndex = 0;
    private final ConfigManager<T> configManager;

    public PlayerManager(int maxPlayers, TeamsInfo<T> teamsInfo, ConfigManager<T> configManager){
        this.maxPlayers = maxPlayers;
        this.teamsInfo = teamsInfo;
        this.configManager = configManager;
        this.prepareTeams();
    }

    private void prepareTeams(){
        if (this.teamsInfo == null || this.teamsInfo.getTeamCount() == 0){
            this.teamsGame = false;
            return;
        }
        this.teamsGame = true;
        HashMap<String, CustomTeam> teams = teamsInfo.getAllowedTeams();
        if (teams.isEmpty()){
            HashMap<String, CustomTeam> allTeams = configManager.getTeams();
            if (this.teamsInfo.getTeamCount() < 0){
                this.allowedTeams.putAll(allTeams);
                this.uniformTeamsList.addAll(allTeams.keySet());
                return;
            }
            if (allTeams.size() < this.teamsInfo.getTeamCount()){
                throw new RuntimeException("Registered teams do not cover the requirements for a game (" + allTeams.size() + "<" + this.teamsInfo.getTeamCount() + ").");
            }
            List<String> teamsKeys = new ArrayList<>(allTeams.keySet());
            while (this.allowedTeams.size() != this.teamsInfo.getTeamCount()){
                String team = teamsKeys.get(RANDOM.nextInt(teamsKeys.size()));
                if (!this.allowedTeams.containsKey(team)){
                    this.allowedTeams.put(team, allTeams.get(team));
                    this.uniformTeamsList.add(team);
                    teamsKeys.remove(team);
                }
            }
            return;
        }
        this.allowedTeams.putAll(teams);
        this.uniformTeamsList.addAll(teams.keySet());
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
        if (this.teamsGame){
            this.joinPlayerToTeam(player);
        }
        this.playersContainer.joinPlayer(player);
        return PlayerState.PLAYING;
    }

    private void joinPlayerToTeam(Player player){
        if (this.teams.containsKey(player)) return;
        switch (this.teamsInfo.getTeamDistribution()){
            case UNIFORM -> {
                String team = this.uniformTeamsList.get(this.uniformIndex);
                this.teams.put(player, team);
                this.uniformIndex++;
                if (this.uniformIndex >= this.uniformTeamsList.size()){
                    this.uniformIndex = 0;
                }
            }
            case RANDOM -> {
                String team = this.uniformTeamsList.get(RANDOM.nextInt(this.uniformTeamsList.size()));
                this.teams.put(player, team);
            }
        }
    }

    /**
     * Joins a player to a team. It doesn't join it to the game.
     * @param player The player.
     * @param team The team key.
     */
    public void joinPlayerToTeam(Player player, String team){
        if (this.playersContainer.isSpectating(player) || this.playersContainer.isPlaying(player)) return;
        if (!this.teamsGame || !this.allowedTeams.containsKey(team) || this.teams.containsKey(player)) return;
        if (this.playersContainer.getPlaying().size() > getMaxPlayersPerGame()) return;
        this.teams.put(player, team);
    }

    /**
     * @param player The player.
     * @return The Custom Team of the player.
     */
    public CustomTeam getTeamOfPlayer(Player player){
        if (!this.teams.containsKey(player)) return null;
        return this.allowedTeams.get(this.teams.get(player));
    }

    /**
     * @return Get all the allowed teams of the game instance.
     */
    @Nullable
    public HashMap<String, CustomTeam> getAllowedTeams(){
        if (this.allowedTeams.isEmpty()) return null;
        return this.allowedTeams;
    }

    /**
     * @param team The team key.
     * @return A list of player inside the team.
     */
    public List<Player> getPlayersOfTeamByName(String team){
        List<Player> players = new ArrayList<>();
        teams.forEach((player, inTeam) -> {
            if (inTeam.equals(team)){
                players.add(player);
            }
        });
        return players;
    }

    /**
     * @param customTeam The custom team.
     * @return A list of player inside the team.
     */
    public List<Player> getPlayersOfTeamByCustomTeam(CustomTeam customTeam){
        if (!getAllowedTeamsList().contains(customTeam)) return null;
        List<Player> players = new ArrayList<>();
        teams.forEach((player, inTeam) -> {
            if (inTeam.equals(getTeamName(customTeam))){
                players.add(player);
            }
        });
        return players;
    }

    /**
     * @return The allowed teams of the game instance.
     */
    public List<CustomTeam> getAllowedTeamsList(){
        return new ArrayList<>(this.allowedTeams.values());
    }

    /**
     * @param customTeam The custom team.
     * @return The team key of the custom team.
     */
    @Nullable
    public String getTeamName(CustomTeam customTeam){
        for (String s : this.allowedTeams.keySet()) {
            CustomTeam team = this.allowedTeams.get(s);
            if (team.equals(customTeam)){
                return s;
            }
        }
        return null;
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
