package net.pulga22.bulb.core.score;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.pulga22.bulb.core.score.components.EmptyComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScoreboard {

    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<GameScoreComponent> components = new ArrayList<>();
    private boolean hided = false;
    private Score defaultScore;
    private final TextComponent title;
    private final String lastRowInfo;

    private GameScoreboard(GameScoreboardInfo info) {
        this.title = info.title();
        this.lastRowInfo = info.lastRow();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = this.scoreboard.registerNewObjective("gameScore" + new Random().nextInt(0,999), Criteria.DUMMY, this.title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        if (info.lastRow() != null){
            this.addDefaultScore(0);
        }
    }

    private void addDefaultScore(int spaces){
        this.defaultScore = this.objective.getScore(" ".repeat(spaces) + this.lastRowInfo);
        this.defaultScore.setScore(-99);
    }

    public void initComponent(int score, GameScoreComponent component){
        component.init(score, this.scoreboard, this.objective, this);
        this.components.add(component);
    }

    public void addEmptyComponents(Integer... scores){
        for (Integer score : scores) {
            this.initComponent(score, new EmptyComponent());
        }
    }

    public void callUpdate(){
        if (this.hided || this.lastRowInfo == null) return;
        final int[] max = {(this.title.content()).length()};
        components.stream().filter(GameScoreComponent::isVisible).forEach(gameScoreComponent -> {
            int size = gameScoreComponent.getScore().getEntry().length();
            if (size > max[0]){
                max[0] = size;
            }
        });
        int spaceToCenter = Math.max((max[0] / 2) - 5, 0);
        this.scoreboard.resetScores(this.defaultScore.getEntry());
        this.addDefaultScore(spaceToCenter);
    }

    public Team registerTeam(String name, String prefix, int color){
        Team team = this.scoreboard.registerNewTeam(name);
        team.prefix(Component.text(prefix, TextColor.color(color)));
        team.setAllowFriendlyFire(false);
        return team;
    }

    public Team registerTeam(String name, String prefix, int color, NamedTextColor glowingColor){
        Team team = this.scoreboard.registerNewTeam(name);
        team.prefix(Component.text(prefix, TextColor.color(color)));
        team.setAllowFriendlyFire(false);
        team.color(glowingColor);
        return team;
    }

    public void showToPlayer(Player player){
        player.setScoreboard(this.scoreboard);
    }

    public void delete(){
        this.hided = true;
        this.components.clear();
        this.defaultScore = null;
        this.objective.unregister();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private TextComponent title = Component.text("GameScoreboard");
        private String lastRow = null;

        public Builder setTitle(TextComponent title){
            this.title = title;
            return this;
        }

        public Builder setLastRow(String lastRow){
            this.lastRow = lastRow;
            return this;
        }

        public GameScoreboard build(){
            return new GameScoreboard(new GameScoreboardInfo(this.title, this.lastRow));
        }

    }

}
