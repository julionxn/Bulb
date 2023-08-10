package net.pulga22.bulb.core.teams;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;

import java.util.Objects;

/**
 * This class represents a team loaded from the configuration.
 */
public class CustomTeam {

    private final String name;
    private final int color;
    private final NamedTextColor glowing;
    @SuppressWarnings("deprecation")
    private final ChatColor chatColor;
    private final String mcColor;

    /**
     * @param name The teamKey
     * @param color The hexadecimal color ("#ff00ff")
     * @param mcColor The minecraft color name.
     */
    public CustomTeam(String name, String color, String mcColor) {
        this.name = name;
        this.color = parseColor(color);
        this.glowing = parseNamedTextColor(mcColor);
        this.chatColor = parseChatColor(mcColor);
        this.mcColor = mcColor;
    }

    public String getName(){
        return this.name;
    }

    public int getColor(){
        return this.color;
    }

    public NamedTextColor getGlowing(){
        return this.glowing;
    }

    @SuppressWarnings("deprecation")
    public ChatColor getChatColor(){
        return this.chatColor;
    }

    private int parseColor(String hexColor){
        String hex = hexColor;
        if (hexColor.startsWith("#")){
            hex = hexColor.substring(1);
        }
        return Integer.parseInt(hex, 16);
    }

    private NamedTextColor parseNamedTextColor(String mcColor){
        return switch (mcColor){
            case "black" -> NamedTextColor.BLACK;
            case "dark_blue" -> NamedTextColor.DARK_BLUE;
            case "dark_green" -> NamedTextColor.DARK_GREEN;
            case "dark_aqua" -> NamedTextColor.DARK_AQUA;
            case "dark_red" -> NamedTextColor.DARK_RED;
            case "dark_purple" -> NamedTextColor.DARK_PURPLE;
            case "gold" -> NamedTextColor.GOLD;
            case "gray" -> NamedTextColor.GRAY;
            case "dark_gray" -> NamedTextColor.DARK_GRAY;
            case "blue" -> NamedTextColor.BLUE;
            case "green" -> NamedTextColor.GREEN;
            case "aqua" -> NamedTextColor.AQUA;
            case "red" -> NamedTextColor.RED;
            case "light_purple" -> NamedTextColor.LIGHT_PURPLE;
            case "yellow" -> NamedTextColor.YELLOW;
            default -> NamedTextColor.WHITE;
        };
    }

    @SuppressWarnings("deprecation")
    private ChatColor parseChatColor(String mcColor){
        return switch (mcColor){
            case "black" -> ChatColor.BLACK;
            case "dark_blue" -> ChatColor.DARK_BLUE;
            case "dark_green" -> ChatColor.DARK_GREEN;
            case "dark_aqua" -> ChatColor.DARK_AQUA;
            case "dark_red" -> ChatColor.DARK_RED;
            case "dark_purple" -> ChatColor.DARK_PURPLE;
            case "gold" -> ChatColor.GOLD;
            case "gray" -> ChatColor.GRAY;
            case "dark_gray" -> ChatColor.DARK_GRAY;
            case "blue" -> ChatColor.BLUE;
            case "green" -> ChatColor.GREEN;
            case "aqua" -> ChatColor.AQUA;
            case "red" -> ChatColor.RED;
            case "light_purple" -> ChatColor.LIGHT_PURPLE;
            case "yellow" -> ChatColor.YELLOW;
            default -> ChatColor.WHITE;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomTeam that = (CustomTeam) o;
        return color == that.color && Objects.equals(name, that.name) && Objects.equals(mcColor, that.mcColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, mcColor);
    }
}
