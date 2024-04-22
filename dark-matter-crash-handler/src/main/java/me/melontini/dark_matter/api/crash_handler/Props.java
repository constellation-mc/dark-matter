package me.melontini.dark_matter.api.crash_handler;

import net.fabricmc.loader.api.FabricLoader;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * An enum of properties to be attached to events.
 * Each property has a getter that retrieves the property value at runtime.
 */
public enum Props implements Prop {
    OS(() -> System.getProperty("os.name")),
    OS_ARCH(() -> System.getProperty("os.arch")),
    OS_VERSION(() -> System.getProperty("os.version")),
    JAVA_VERSION(() -> System.getProperty("java.version")),
    JAVA_VENDOR(() -> System.getProperty("java.vendor")),
    ENVIRONMENT(() -> FabricLoader.getInstance().getEnvironmentType().toString().toLowerCase(Locale.ROOT)),
    MINECRAFT_VERSION(() -> FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getMetadata().getVersion().getFriendlyString());

    private final Supplier<String> getter;

    Props(Supplier<String> getter) {
        this.getter = getter;
    }

    @Override
    public String get() {
        return getter.get();
    }
}
