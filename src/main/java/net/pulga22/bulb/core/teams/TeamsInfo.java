package net.pulga22.bulb.core.teams;

import net.pulga22.bulb.core.config.ConfigManager;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * An auxiliary class that represents the information respective to a game instance about the teams.
 * @param <T> extends Plugin.
 */
public class TeamsInfo<T extends Plugin> {

    private int teamCount = 0;
    private final HashMap<String, CustomTeam> allowedTeams = new HashMap<>();
    private TeamsDistribution teamsDistribution = TeamsDistribution.UNIFORM;
    private final ConfigManager<T> configManager;

    public TeamsInfo(ConfigManager<T> configManager) {
        this.configManager = configManager;
    }

    /**
     * @param count The maximum number of teams allowed within a game instance.
     */
    public void setTeamCount(int count){
        this.teamCount = count;
    }

    public void addAllowedTeam(String teamKey){
        HashMap<String, CustomTeam> teams = configManager.getTeams();
        if (teams.containsKey(teamKey) && this.allowedTeams.keySet().size() < this.teamCount + 1){
            this.allowedTeams.put(teamKey, teams.get(teamKey));
        }
    }

    public void addAllowedTeam(CustomTeam customTeam){
        if (this.allowedTeams.keySet().size() < this.teamCount + 1){
            this.allowedTeams.put(customTeam.getName(), customTeam);
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
