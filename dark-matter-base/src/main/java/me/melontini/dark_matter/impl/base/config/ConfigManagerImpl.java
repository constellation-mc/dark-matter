package me.melontini.dark_matter.impl.base.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.base.config.ConfigManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigManagerImpl<T> implements ConfigManager<T> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Class<T> cls;
    private final String name;
    private final Supplier<T> constructor;

    private final Set<Consumer<JsonObject>> fixers = new LinkedHashSet<>();

    private final Set<Listener<T>> save = new LinkedHashSet<>();
    private final Set<Listener<T>> load = new LinkedHashSet<>();

    private final Set<Handler> handlers = new LinkedHashSet<>();

    public ConfigManagerImpl(Class<T> cls, String name, Supplier<T> constructor) {
        this.cls = cls;
        this.name = name;
        this.constructor = constructor;
    }

    @Override
    public ConfigManager<T> fixup(Consumer<JsonObject> fixer) {
        fixers.add(fixer);
        return this;
    }

    @Override
    public T createDefault() {
        return this.constructor.get();
    }

    @Override
    public T load(Path root) {
        var path = resolve(root);

        AtomicReference<T> config = new AtomicReference<>();
        if (Files.exists(path)) {
            try (var reader = Files.newBufferedReader(path)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                fixers.forEach(fixer -> fixer.accept(json));
                config.set(GSON.fromJson(json, type()));
            } catch (Exception e) {
                handlers.forEach(handler -> handler.accept(e, Stage.LOAD, path));
                config.set(createDefault());
            }
        } else {
            config.set(createDefault());
        }
        load.forEach(listener -> listener.accept(config.get(), path));
        return config.get();
    }

    @Override
    public void save(Path root, T config) {
        var path = resolve(root);

        save.forEach(listener -> listener.accept(config, path));

        try {
            Files.createDirectories(path.getParent());
            byte[] cfg = GSON.toJson(config).getBytes();
            if (Files.exists(path)) {
                byte[] old = Files.readAllBytes(path);
                if (Arrays.equals(old, cfg)) {
                    return;
                }
            }
            Files.write(path, cfg);
        } catch (Exception e) {
            handlers.forEach(handler -> handler.accept(e, Stage.SAVE, path));
        }
    }

    public Path resolve(Path root) {
        return root.resolve(name() + ".json");
    }

    @Override
    public ConfigManager<T> onSave(Listener<T> listener) {
        save.add(listener);
        return this;
    }

    @Override
    public ConfigManager<T> onLoad(Listener<T> listener) {
        load.add(listener);
        return this;
    }

    @Override
    public ConfigManager<T> exceptionHandler(Handler handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public Class<T> type() {
        return this.cls;
    }

    @Override
    public String name() {
        return this.name;
    }
}
