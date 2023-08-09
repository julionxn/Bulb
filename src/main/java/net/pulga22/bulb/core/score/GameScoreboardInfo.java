package net.pulga22.bulb.core.score;

import net.kyori.adventure.text.TextComponent;

public class GameScoreboardInfo {

    private final TextComponent prefix;
    private final int gameColor;
    private final String lastRow;
    private final boolean empty;

    public GameScoreboardInfo(TextComponent prefix, int gameColor, String lastRow) {
        this.prefix = prefix;
        this.gameColor = gameColor;
        this.lastRow = lastRow;
        this.empty = false;
    }

    private GameScoreboardInfo() {
        this.prefix = null;
        this.gameColor = 0;
        this.lastRow = null;
        this.empty = true;
    }

    public TextComponent prefix(){
        return this.prefix;
    }

    public int gameColor(){
        return this.gameColor;
    }

    public String lastRow(){
        return this.lastRow;
    }

    public boolean empty(){
        return this.empty;
    }

    public static GameScoreboardInfo ofEmpty(){
        return new GameScoreboardInfo();
    }

}
