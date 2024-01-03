package me.melontini.dark_matter.impl.crash_handler.uploading;

import me.melontini.dark_matter.api.base.config.ConfigManager;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Config {

    private static final ConfigManager<RawConfig> CONFIG_MANAGER = ConfigManager.of(RawConfig.class, "dark-matter/crash_upload", RawConfig::new)
            .exceptionHandler((e, stage) -> {
                throw new RuntimeException("Failed to %s dark-matter/crash_upload!".formatted(stage.toString().toLowerCase()));
            });
    private static final ReadConfig CONFIG = loadConfig();

    private static ReadConfig loadConfig() {
        Path cd = FabricLoader.getInstance().getConfigDir();
        if (Files.exists(cd.resolve("dark-matter/analytics.json"))) {
            try {
                Files.move(cd.resolve("dark-matter/analytics.json"), CONFIG_MANAGER.resolve(cd), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                DarkMatterLog.error("Failed to move config!", e);
            }
        }

        RawConfig config = CONFIG_MANAGER.load(cd);
        CONFIG_MANAGER.save(FabricLoader.getInstance().getConfigDir(), config);

        return new ReadConfig(config.enabled);
    }

    public static void init() {

    }

    public static boolean enabled() {
        return CONFIG.enabled();
    }

    private record ReadConfig(boolean enabled) {

    }

    private static class RawConfig {
        public boolean enabled = true;
    }
}
