package dev.doublekekse.confetti.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfettiConfig {
    public static boolean ENABLED = true;

    private static Path getPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("confetti.json5");
    }

    public static void save() throws IOException {
        var filePath = getPath();

        //noinspection ResultOfMethodCallIgnored
        filePath.getParent().toFile().mkdirs();
        Files.writeString(filePath, "{\"enabled\":" + ENABLED + "}");
    }

    // High quality json parsing right here
    public static void load() throws IOException {
        var filePath = getPath();

        if (!Files.exists(filePath)) {
            save();
            return;
        }

        var json = Files.readString(filePath, StandardCharsets.UTF_8);
        ENABLED = json.contains("true");
    }
}
