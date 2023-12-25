package me.melontini.dark_matter.impl.analytics;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class AnalyticsInternals {

    private static final ConfigManager<Config> CONFIG_MANAGER = ConfigManager.of(Config.class, "dark-matter/analytics", Config::new)
            .exceptionHandler((e, stage) -> {
                throw new RuntimeException("Failed to %s dark-matter/analytics!".formatted(stage.toString().toLowerCase()));
            });
    private static final ReadConfig CONFIG = loadConfig();
    private static UUID oldID = null;

    private static ReadConfig loadConfig() {
        Config config = CONFIG_MANAGER.load(FabricLoader.getInstance().getConfigDir());
        oldID = config.userUUID;
        CONFIG_MANAGER.save(FabricLoader.getInstance().getConfigDir(), config);

        return new ReadConfig(config.enabled,  config.crashesEnabled);
    }

    public static boolean canSend() {
        return enabled() || handleCrashes();
    }

    public static boolean enabled() {
        return CONFIG.enabled();
    }

    public static boolean handleCrashes() {
        return CONFIG.crashesEnabled();
    }

    public static Optional<UUID> getOldID() {
        return Optional.ofNullable(oldID);
    }

    public static void init() {
        // Init the config on PreLaunch.
    }

    private record ReadConfig(boolean enabled, boolean crashesEnabled) {

    }

    private static class Config {
        public boolean enabled = true;
        public boolean crashesEnabled = true;
        @Deprecated
        public UUID userUUID;
    }
}
