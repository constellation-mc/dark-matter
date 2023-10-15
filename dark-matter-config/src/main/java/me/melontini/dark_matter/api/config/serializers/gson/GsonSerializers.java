package me.melontini.dark_matter.api.config.serializers.gson;

import com.google.gson.Gson;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import me.melontini.dark_matter.impl.config.GsonSerializer;

import java.util.function.Supplier;

public class GsonSerializers {

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager) {
        return new GsonSerializer<>(manager, defaultCtx(manager));
    }

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager, Supplier<T> ctx) {
        return new GsonSerializer<>(manager, ctx);
    }

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager, Supplier<T> ctx, Gson gson) {
        return new GsonSerializer<>(manager, ctx, gson);
    }

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager, Fixups fixups) {
        return new GsonSerializer<>(manager, defaultCtx(manager)).setFixups(fixups);
    }

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager, Supplier<T> ctx, Fixups fixups) {
        return new GsonSerializer<>(manager, ctx).setFixups(fixups);
    }

    public static <T> ConfigSerializer<T> create(ConfigManager<T> manager, Supplier<T> ctx, Gson gson, Fixups fixups) {
        return new GsonSerializer<>(manager, ctx, gson).setFixups(fixups);
    }

    private static <T> Supplier<T> defaultCtx(ConfigManager<T> manager) {
        return () -> {
            try {
                return Reflect.setAccessible(manager.getType().getDeclaredConstructor()).newInstance();
            } catch (Throwable t) {
                throw new RuntimeException("Failed to construct config class", t);
            }
        };
    }
}
