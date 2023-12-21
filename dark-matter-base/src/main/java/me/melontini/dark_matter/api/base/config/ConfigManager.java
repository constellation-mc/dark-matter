package me.melontini.dark_matter.api.base.config;

import com.google.gson.JsonObject;
import me.melontini.dark_matter.impl.base.config.ConfigManagerImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConfigManager<T> {

    static <T> ConfigManager<T> of(Class<T> type, String name) {
        return new ConfigManagerImpl<>(type, name, () -> {
            try {
                return type.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });
    }

    static <T> ConfigManager<T> of(Class<T> type, String name, Supplier<T> constructor) {
        return new ConfigManagerImpl<>(type, name, constructor);
    }

    ConfigManager<T> fixup(Consumer<JsonObject> fixer);

    T createDefault();
    T load(Path root) throws IOException;
    void save(Path root, T config) throws IOException;

    ConfigManager<T> onSave(State state, Listener<T> listener);
    ConfigManager<T> onLoad(Listener<T> listener);

    Class<T> type();
    String name();

    interface Listener<T> extends Consumer<T> {
        void accept(T config);
    }

    enum State {
        PRE, POST
    }
}
