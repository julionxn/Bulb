package net.pulga22.bulb.core.config;

import net.pulga22.bulb.core.teams.CustomTeam;
import net.pulga22.bulb.core.worlds.WorldOption;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;


/**
 * <p>This class is used for the creation and manipulation of a plugin configuration. <br>
 * It creates and loads the minimum configuration required for the plugin and game instances to work. <br>
 * You can create a class that inherits from this one to add extra configuration. <br>
 * Singleton pattern strongly recommended.</p>
 * @see #tryLoad(String, BiConsumer, BiConsumer)
 * @see #tryLoad(String, BiConsumer, BiConsumer, Consumer)
 * @param <T> A plugin.
 */
public class ConfigManager<T extends Plugin> {

    protected static final Random RANDOM = new Random();
    protected static final String[] emptyArray = new String[0];
    protected final T plugin;
    protected final Logger logger;
    protected File configFolder;
    protected File templatesFolder;
    protected File gamesFolder;
    private FileConfiguration worldsConfig;
    private FileConfiguration teamsConfig;
    private final HashMap<String, HashSet<WorldOption>> loadedWorlds = new HashMap<>();
    private final HashMap<String, CustomTeam> loadedTeams = new HashMap<>();

    protected ConfigManager(T plugin){
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.setup(plugin);
    }

    public static <P extends Plugin> ConfigManager<P> register(@NotNull P plugin){
        return new ConfigManager<>(plugin);
    }

    /**
     * Loads the world and teams.
     * @param plugin The plugin.
     */
    private void setup(T plugin){
        final File configFolder = plugin.getDataFolder();
        boolean canProceed = true;
        if (!configFolder.exists()) canProceed = configFolder.mkdir();
        if (!canProceed) throw new RuntimeException("Config folder can't be found.");
        this.configFolder = configFolder;
        this.templatesFolder = new File(configFolder.toString(), "templates");
        boolean templatesFolderCreated = templatesFolder.mkdir();
        if (templatesFolderCreated){
            this.logger.info("Templates folder created.");
        }
        gamesFolder = new File(configFolder.toString(), "games");
        boolean gamesFolderCreated = gamesFolder.mkdir();
        if (gamesFolderCreated){
            this.logger.info("Games folder created.");
        }
        this.tryLoad("worlds.yml",
                (file, config) -> {
                    config.addDefault("worlds", emptyArray);
                    config.options().copyDefaults(true);
                    this.save(config, file);
                    this.logger.info("Worlds config file created.");
                },
                (file, config) -> {
                    this.worldsConfig = config;
                    this.logger.info("Worlds config file loaded...");
                    this.parseWorlds();
                });
        this.tryLoad("teams.yml",
                (file, config) -> {
                    config.addDefault("teams", emptyArray);
                    config.options().copyDefaults(true);
                    this.save(config, file);
                    this.logger.info("Teams config file created.");
                },
                (file, config) -> {
                    this.teamsConfig = config;
                    this.logger.info("Teams config file loaded...");
                    this.parseTeams();
                });
    }

    /**
     * Saves a FileConfiguration onto a File.
     * @param config The FileConfiguration to be saved.
     * @param file The file where is going to be saved.
     */
    public void save(@NotNull FileConfiguration config, @NotNull File file){
        try {
            config.save(file);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to load a config file (.yml), if it doesn't exist, creates one.
     * @param file The file name.
     * @param onNew Actions taken when te file is first time created.
     * @param onLoaded Actions taken when the file already exists.
     * @param onException On a IOException
     */
    protected void tryLoad(@NotNull String file, BiConsumer<File, FileConfiguration> onNew, BiConsumer<File, FileConfiguration> onLoaded, Consumer<Exception> onException){
        if (!file.endsWith(".yml")){
            file = file + ".yml";
        }
        final File configFile = new File(this.configFolder, file);
        try {
            boolean created = configFile.createNewFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            if (created){
                onNew.accept(configFile, config);
            } else {
                onLoaded.accept(configFile, config);
            }
        } catch (IOException e){
            onException.accept(e);
        }
    }

    /**
     * Tries to load a config file (.yml), if it doesn't exist, creates one.
     * @param file The file name.
     * @param onNew Actions taken when te file is first time created.
     * @param onLoaded Actions taken when the file already exists.
     */
    protected void tryLoad(@NotNull String file, BiConsumer<File, FileConfiguration> onNew, BiConsumer<File, FileConfiguration> onLoaded){
        this.tryLoad(file, onNew, onLoaded, (exception) -> {throw new RuntimeException(exception);});
    }

    private void parseWorlds(){
        final ConfigurationSection mainSection = this.worldsConfig.getConfigurationSection("worlds");
        if (mainSection == null){
            throw new MalformedConfigFile("worlds.yml");
        }
        final Set<String> loadedGames = mainSection.getKeys(false);
        loadedGames.forEach(gameKey -> {
            ConfigurationSection gameSection = mainSection.getConfigurationSection(gameKey);
            if (gameSection == null) return;
            final Set<String> loadedWorlds = gameSection.getKeys(false);
            loadedWorlds.forEach(worldKey -> {
                ConfigurationSection world = gameSection.getConfigurationSection(worldKey);
                if (world == null) return;
                String worldName = world.getString("name");
                WorldOption worldOption = new WorldOption(worldName, this.templatesFolder.toPath(), this.gamesFolder.toPath());
                ConfigurationSection poisSection = world.getConfigurationSection("pois");
                if (poisSection != null){
                    Set<String> pois = poisSection.getKeys(false);
                    pois.forEach(poiKey -> {
                        List<Double> coords = poisSection.getDoubleList(poiKey);
                        if (coords.size() == 3 || coords.size() == 4){
                            worldOption.addPointOfInterest(poiKey, coords.get(0), coords.get(1), coords.get(2));
                        }
                        if (coords.size() == 5){
                            worldOption.addPointOfInterest(poiKey, coords.get(0), coords.get(1), coords.get(2),
                                    coords.get(3).floatValue(), coords.get(4).floatValue());
                        }
                    });
                }
                this.loadedWorlds.computeIfAbsent(gameKey, key -> new HashSet<>());
                this.loadedWorlds.get(gameKey).add(worldOption);
                this.logger.info(">     World " + worldName + " loaded.");
            });
        });
    }

    private void parseTeams(){
        final ConfigurationSection mainSection = this.teamsConfig.getConfigurationSection("teams");
        if (mainSection == null){
            throw new MalformedConfigFile("teams.yml");
        }
        final Set<String> loadedTeams = mainSection.getKeys(false);
        loadedTeams.forEach(teamKey -> {
            ConfigurationSection teamSection = mainSection.getConfigurationSection(teamKey);
            if (teamSection == null) return;
            String teamName = teamSection.getString("name");
            String color = teamSection.getString("color");
            String mcColor = teamSection.getString("mc_color");
            CustomTeam team = new CustomTeam(teamName, color, mcColor);
            this.loadedTeams.put(teamKey, team);
            this.logger.info(">     Team " + teamName + " loaded.");
        });
    }

    /**
     * @param game The gameName.
     * @return The worldOptions of that game.
     */
    @Nullable
    public final HashSet<WorldOption> getWorldsOfGame(String game){
        return this.loadedWorlds.get(game);
    }

    /**
     * @return Gets the loaded teams from the config,
     */
    public final HashMap<String, CustomTeam> getTeams(){
        return this.loadedTeams;
    }

    /**
     * @return A random team key.
     */
    public final String getRandomTeam(){
        String[] teams = this.loadedTeams.keySet().toArray(new String[0]);
        return teams[RANDOM.nextInt(teams.length)];
    }

    /**
     * @param name The team key.
     * @return The custom team according to the key.
     */
    @Nullable
    public final CustomTeam getTeamOf(String name){
        return this.loadedTeams.get(name);
    }

    protected static class MalformedConfigFile extends RuntimeException{
        public MalformedConfigFile(String file) {
            super("Malformed config file (" + file + ").");
        }
    }
}
