package me.melontini.dark_matter.api.base.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.base.util.Context;
import me.melontini.dark_matter.impl.base.config.ConfigManagerImpl;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConfigManager<T> {

    Context.Key<Gson> GSON = Context.key("gson");

    static <T> ConfigManager<T> of(Class<T> type, String name) {
        return new ConfigManagerImpl<>(type, name, () -> {
            try {
                return type.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to construct %s".formatted(type.getName()), e);
            }
        });
    }

    static <T> ConfigManager<T> of(Class<T> type, String name, Supplier<T> constructor) {
        return new ConfigManagerImpl<>(type, name, constructor);
    }

    ConfigManager<T> fixup(Consumer<JsonObject> fixer);

    T createDefault();
    T load(Path root, Context context);
    void save(Path root, T config, Context context);

    Path resolve(Path root);

    /**
     * Executed right before the config is saved.
     * Can be used to force options, e.g. if an incompatible mod is loaded.
     * <p>The listener is invoked on the thread where was {@code onSave} called</p>
     */
    ConfigManager<T> onSave(Listener<T> listener);

    /**
     * Executed right before the config is returned by {@code load}.
     * Generally not very useful, since it's usually only executed once.
     * <p>The listener is invoked on the thread where was {@code onLoad} called</p>
     */
    ConfigManager<T> onLoad(Listener<T> listener);

    /**
     * Handle IOExceptions thrown by the config manager.
     * If none of the handlers throw an exception during {@code load}, the default config is returned.
     */
    ConfigManager<T> exceptionHandler(Handler handler);

    Class<T> type();
    String name();

    @FunctionalInterface
    interface Listener<T> {
        void accept(T config, Path path);
    }

    @FunctionalInterface
    interface Handler {
        void accept(Exception e, Stage stage, Path path);
    }

    enum Stage {
        SAVE,
        LOAD
    }
}
