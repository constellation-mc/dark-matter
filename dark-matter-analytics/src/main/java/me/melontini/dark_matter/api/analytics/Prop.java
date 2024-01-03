package me.melontini.dark_matter.api.analytics;

import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;
/**
 * An enum of properties to be attached to events.
 * Each property has a getter that retrieves the property value at runtime.
 */
public enum Prop {
    OS(() -> System.getProperty("os.name")),
    OS_ARCH(() -> System.getProperty("os.arch")),
    OS_VERSION(() -> System.getProperty("os.version")),
    JAVA_VERSION(() -> System.getProperty("java.version")),
    JAVA_VENDOR(() -> System.getProperty("java.vendor")),
    ENVIRONMENT(() -> FabricLoader.getInstance().getEnvironmentType().toString().toLowerCase()),
    MINECRAFT_VERSION(() -> FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getMetadata().getVersion().getFriendlyString());

    private final Supplier<String> getter;

    Prop(Supplier<String> getter) {
        this.getter = getter;
    }

    public String get() {
        return getter.get();
    }
}
