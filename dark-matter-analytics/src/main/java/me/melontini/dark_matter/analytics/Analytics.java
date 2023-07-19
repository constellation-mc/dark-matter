package me.melontini.dark_matter.analytics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.melontini.dark_matter.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;


public class Analytics {
    private static final UUID nullID = new UUID(0,0);
    private static final Config CONFIG;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path CU_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("cracker-util/analytics.properties");
    private static final Path OLD_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dark-matter/analytics.properties");

    static {
        moveConfigIfExists();
        CONFIG = loadConfig();
    }

    private static void moveConfigIfExists() {
        if (Files.exists(CU_CONFIG_PATH)) {
            try {
                if (!Files.exists(OLD_CONFIG_PATH.getParent())) Files.createDirectories(OLD_CONFIG_PATH.getParent());
                DarkMatterLog.info("Found old config at config/cracker_analytics.properties, moving to config/dark-matter/analytics.properties");
                Files.move(CU_CONFIG_PATH, OLD_CONFIG_PATH);
            } catch (IOException e) {
                DarkMatterLog.error("Couldn't move old config!", e);
            }
        }
    }

    private static void upgradeToJson(Config config) {
        if (Files.exists(OLD_CONFIG_PATH)) {
            Properties properties = new Properties();
            try {
                properties.load(Files.newInputStream(OLD_CONFIG_PATH));
                config.enabled = Boolean.parseBoolean(properties.getProperty("enabled"));
                config.userUUID = UUID.fromString(properties.getProperty("user_id"));

                Files.deleteIfExists(OLD_CONFIG_PATH);
            } catch (IOException e) {
                DarkMatterLog.error("Could not read analytics properties", e);
            }
        }
    }

    private static Config loadConfig() {
        Config config = new Config();
        upgradeToJson(config);
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("dark-matter/analytics.json");
        if (Files.exists(configPath)) {
            try {
                config = GSON.fromJson(Files.newBufferedReader(configPath), Config.class);
                Files.write(configPath, GSON.toJson(config).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Files.createDirectories(configPath.getParent());
                Files.createFile(configPath);
                Files.write(configPath, GSON.toJson(config).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!config.enabled) config.userUUID = nullID;
        return config;
    }

    public static UUID getUUID() {
        return CONFIG.userUUID;
    }

    public static String getUUIDString() {
        return CONFIG.userUUID.toString();
    }

    public static boolean isEnabled() {
        return CONFIG.enabled;
    }

    public static boolean handleCrashes() {
        return CONFIG.crashesEnabled;
    }

    public static class Config {
        public boolean enabled = true;
        public boolean crashesEnabled = true;
        public UUID userUUID = nullID;
    }
}
