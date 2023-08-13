package net.pulga22.bulb.core.score.components.counter;

import net.pulga22.bulb.core.score.components.SimpleComponent;
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
        return this.objective.getScore(prefix + this.points);
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

}
