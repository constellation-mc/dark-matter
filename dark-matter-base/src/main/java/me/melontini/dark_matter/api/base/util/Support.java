package me.melontini.dark_matter.api.base.util;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.functions.ThrowingSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@UtilityClass
public class Support {

    public EnvType environment() {
        return FabricLoader.getInstance().getEnvironmentType();
    }

    public <T, F extends T, S extends T> T support(String mod, Supplier<F> expected, Supplier<S> fallback) {
        return support(FabricLoader.getInstance().isModLoaded(mod), expected, fallback);
    }

    public <T, F extends T, S extends T> T fallback(String mod, ThrowingSupplier<F, Throwable> expected, Supplier<S> fallback) {
        return fallback(FabricLoader.getInstance().isModLoaded(mod), expected, fallback);
    }

    public <T, F extends T, S extends T> T support(EnvType cond, Supplier<F> expected, Supplier<S> fallback) {
        return support(cond == environment(), expected, fallback);
    }

    public <T, F extends T, S extends T> T fallback(EnvType cond, ThrowingSupplier<F, Throwable> expected, Supplier<S> fallback) {
        return fallback(cond == environment(), expected, fallback);
    }

    public <T, F extends T, S extends T> T support(boolean cond, Supplier<F> expected, Supplier<S> fallback) {
        return cond ? expected.get() : fallback.get();
    }

    public <T, F extends T, S extends T> T fallback(boolean cond, ThrowingSupplier<F, Throwable> expected, Supplier<S> fallback) {
        try {
            return cond ? expected.get() : fallback.get();
        } catch (Throwable e) {
            return fallback.get();
        }
    }

    public void share(String id, Object o) {
        FabricLoader.getInstance().getObjectShare().put(id, o);
    }

    public <T> void whenAvailable(String id, Class<T> type /*generics moment*/, BiConsumer<String, T> consumer) {
        FabricLoader.getInstance().getObjectShare().whenAvailable(id, (s, o) -> {
            if (type.isInstance(o)) consumer.accept(s, type.cast(o));
        });
    }

    public <T> void whenAvailable(EnvType envType, String id, Class<T> type, BiConsumer<String, T> consumer) {
        if (environment() == envType) FabricLoader.getInstance().getObjectShare().whenAvailable(id, (s, o) -> {
            if (type.isInstance(o)) consumer.accept(s, type.cast(o));
        });
    }
}
