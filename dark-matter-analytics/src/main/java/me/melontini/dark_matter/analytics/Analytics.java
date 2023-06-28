package me.melontini.dark_matter.analytics;

import me.melontini.dark_matter.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;

@SuppressWarnings("unused")
public class Analytics {
    private static final UUID nullID = new UUID(0,0);
    private static UUID userUUID = nullID;
    private static boolean enabled = true;

    static {
        Properties properties = new Properties();
        Path oldConfig = FabricLoader.getInstance().getConfigDir().resolve("cracker-util/analytics.properties");
        Path config = FabricLoader.getInstance().getConfigDir().resolve("dark-matter/analytics.properties");
        if (Files.exists(oldConfig)) {
            try {
                if (!Files.exists(config.getParent())) Files.createDirectories(config.getParent());
                DarkMatterLog.info("Found old config at config/cracker_analytics.properties, moving to config/dark-matter/analytics.properties");
                Files.move(oldConfig, config);
            } catch (IOException e) {
                DarkMatterLog.error("Couldn't move old config!", e);
            }
        }

        if (Files.exists(config)) {
            try {
                properties.load(Files.newInputStream(config));
                enabled = Boolean.parseBoolean(properties.getProperty("enabled"));
                if (isEnabled()) userUUID = UUID.fromString(properties.getProperty("user_id"));
            } catch (IOException e) {
                DarkMatterLog.error("Could not read analytics properties", e);
            }
        } else {
            try {
                enabled = true;
                properties.setProperty("enabled", "true");
                userUUID = UUID.randomUUID();
                properties.setProperty("user_id", userUUID.toString());
                if (!Files.exists(config.getParent())) Files.createDirectories(config.getParent());
                properties.store(Files.newOutputStream(config), "Dark Matter analytics properties");
            } catch (IOException e) {
                DarkMatterLog.error("Could not write analytics properties", e);
            }
        }
    }

    public static UUID getUUID() {
        return userUUID;
    }

    public static String getUUIDString() {
        return userUUID.toString();
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
