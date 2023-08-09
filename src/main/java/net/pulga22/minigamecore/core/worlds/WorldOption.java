package net.pulga22.minigamecore.core.worlds;


import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 * <p>A class that represents an option for a game instance.</p>
 * Contains the points of interest loaded from configuration
 */
public class WorldOption {

    private final String worldName;
    private final HashMap<String, PointOfInterest> pointOfInterests = new HashMap<>();
    private final Path templatesFolder;
    private final Path gamesFolder;

    public WorldOption(String worldName, Path templatesFolder, Path gamesFolder) {
        this.worldName = worldName;
        this.templatesFolder = templatesFolder;
        this.gamesFolder = gamesFolder;
    }

    public String getWorldName(){
        return this.worldName;
    }

    public void addPointOfInterest(String path, PointOfInterest pointOfInterest){
        this.pointOfInterests.put(path, pointOfInterest);
    }

    public void addPointOfInterest(String path, double x, double y, double z){
        this.pointOfInterests.put(path, new PointOfInterest(x, y, z, 0, 0));
    }

    public void addPointOfInterest(String path, double x, double y, double z, float pitch, float yaw){
        this.pointOfInterests.put(path, new PointOfInterest(x, y, z, pitch, yaw));
    }

    public PointOfInterest getPointOfInterest(String path){
        return this.pointOfInterests.get(path);
    }

    public PointOfInterest getRandomPointOfInterest(){
        Random random = new Random();
        String[] values = this.pointOfInterests.keySet().toArray(new String[0]);
        String selected = values[random.nextInt(values.length)];
        return this.pointOfInterests.get(selected);
    }

    public Collection<PointOfInterest> getPointsOfInterest(){
        return this.pointOfInterests.values();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldOption that = (WorldOption) o;
        return Objects.equals(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName);
    }

    public Path getWorldTemplatePath() {
        return this.templatesFolder.resolve(worldName);
    }

    public Path getGamesPath() {
        return this.gamesFolder;
    }
}
