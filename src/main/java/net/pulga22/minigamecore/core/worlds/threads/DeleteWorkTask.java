package net.pulga22.minigamecore.core.worlds.threads;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class DeleteWorkTask implements Runnable{

    private final Logger logger;
    private final Path targetDir;
    private final Runnable onReady;

    private DeleteWorkTask(Plugin plugin, Path targetDir, Runnable onReady) {
        this.logger = plugin.getLogger();
        this.targetDir = targetDir;
        this.onReady = onReady;
    }

    public static DeleteWorkTask of(Plugin plugin, Path sourceDir, Runnable onReady) {
        return new DeleteWorkTask(plugin, sourceDir, onReady);
    }

    @Override
    public void run() {
        this.logger.info(">     Deleting files in a new thread...");
        try {
            if (Files.exists(this.targetDir)) {
                try (Stream<Path> paths = Files.walk(this.targetDir)) {
                    paths.sorted(Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    throw new RuntimeException("Something occurred deleting a file.", e);
                                }
                            });
                }
            } else {
                throw new RuntimeException("Folder doesnt exits.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Something occurred deleting a world folder.", e);
        }
        this.logger.info(">     All files deleted in a new thread.");
        this.onReady.run();
    }
}
