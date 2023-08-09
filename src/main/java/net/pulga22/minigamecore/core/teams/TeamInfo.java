package net.pulga22.minigamecore.core.teams;

import net.pulga22.minigamecore.core.config.ConfigManager;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * An auxiliary class that represents the information respective to a game instance about the teams.
 * @param <T> extends Plugin.
 */
public class TeamInfo<T extends Plugin> {

    private int teamCount = 0;
    private final HashMap<String, CustomTeam> allowedTeams = new HashMap<>();
    private TeamsDistribution teamsDistribution = TeamsDistribution.UNIFORM;
    private final ConfigManager<T> configManager;

    public TeamInfo(ConfigManager<T> configManager) {
        this.configManager = configManager;
    }

    public void setTeamCount(int count){
        this.teamCount = count;
    }

    public void addAllowTeam(String teamName){
        HashMap<String, CustomTeam> teams = configManager.getTeams();
        if (teams.containsKey(teamName)){
            this.allowedTeams.put(teamName, teams.get(teamName));
        }
    }

    public void setDistribution(TeamsDistribution distribution){
        this.teamsDistribution = distribution;
    }

    public int getTeamCount() {
        return this.teamCount;
    }

    public HashMap<String, CustomTeam> getAllowedTeams() {
        return this.allowedTeams;
    }

    public TeamsDistribution getTeamDistribution() {
        return this.teamsDistribution;
    }
}
