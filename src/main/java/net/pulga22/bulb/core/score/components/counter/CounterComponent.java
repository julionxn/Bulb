package net.pulga22.bulb.core.score.components.counter;

import net.pulga22.bulb.core.score.components.SimpleComponent;
import net.pulga22.bulb.core.teams.CustomTeam;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;

public class CounterComponent extends SimpleComponent {

    protected final String prefix;
    protected int points;

    public CounterComponent(String prefix) {
        this(prefix, 0);
    }

    public CounterComponent(String prefix, int starting) {
        this.prefix = prefix;
        this.points = starting;
    }

    @Override
    public Score getScore() {
        return this.objective.getScore(prefix + ChatColor.WHITE + this.points);
    }

    public void increment(){
        this.points++;
        this.update();
    }

    public void decrement(){
        this.points--;
        this.update();
    }

    public void add(int amount){
        this.points += amount;
        this.update();
    }

    public void remove(int amount){
        this.points -= amount;
        this.update();
    }

    public void reset(){
        this.points = 0;
        this.update();
    }

    public int get(){
        return this.points;
    }

    public static CounterComponent of(CustomTeam customTeam){
        return new CounterComponent(customTeam.getChatColor() + customTeam.getName());
    }

    public static CounterComponent of(CustomTeam customTeam, int starting){
        return new CounterComponent(customTeam.getChatColor() + customTeam.getName(), starting);
    }

}
