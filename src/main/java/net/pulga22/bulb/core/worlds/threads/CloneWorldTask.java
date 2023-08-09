package net.pulga22.bulb.core.worlds.threads;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

public class CloneWorldTask implements Runnable {

    private final Logger logger;
    private final Path sourceDir;
    private final Path targetDir;
    private final Runnable onReady;

    private CloneWorldTask(Plugin plugin, Path sourceDir, Path targetDir, Runnable onReady) {
        this.logger = plugin.getLogger();
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
        this.onReady = onReady;
    }

    public static CloneWorldTask of(Plugin plugin, Path sourceDir, Path targetDir, Runnable onReady) {
        return new CloneWorldTask(plugin, sourceDir, targetDir, onReady);
    }

    @Override
    public void run() {
        this.logger.info(">     Cloning files in a new thread...");
        try {
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = targetDir.resolve(sourceDir.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir2 = targetDir.resolve(sourceDir.relativize(dir));
                    Files.createDirectories(targetDir2);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Something occurred while cloning a worlds file.", e);
        }
        this.logger.info(">     All files cloned in a new thread.");
        this.onReady.run();
    }
}
