package me.melontini.dark_matter.impl.analytics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;


public class AnalyticsInternals {
    private static final UUID nullID = new UUID(0, 0);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path OLD_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dark-matter/analytics.properties");
    private static final Config CONFIG;

    static {
        CONFIG = loadConfig();
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
                if (config.enabled && nullID.equals(config.userUUID)) config.userUUID = UUID.randomUUID();
                if (!config.enabled) config.userUUID = nullID;
                Files.write(configPath, GSON.toJson(config).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Files.createDirectories(configPath.getParent());
                if (config.enabled) config.userUUID = UUID.randomUUID();
                Files.write(configPath, GSON.toJson(config).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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

    private static class Config {
        public boolean enabled = true;
        public boolean crashesEnabled = true;
        public UUID userUUID = nullID;
    }
}
