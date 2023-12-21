package me.melontini.dark_matter.impl.base.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.base.config.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigManagerImpl<T> implements ConfigManager<T> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Class<T> cls;
    private final String name;
    private final Supplier<T> constructor;

    private final Set<Consumer<JsonObject>> fixers = new HashSet<>();

    private final Map<State, Set<Listener<T>>> save = new HashMap<>();
    private final Set<Listener<T>> load = new HashSet<>();

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
    public T load(Path root) throws IOException {
        var path = resolve(root);

        T config;
        if (Files.exists(path)) {
            try (var reader = Files.newBufferedReader(path)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                fixers.forEach(fixer -> fixer.accept(json));
                config = GSON.fromJson(json, type());
            }
        } else {
            config = createDefault();
        }
        load.forEach(listener -> listener.accept(config));
        return config;
    }

    @Override
    public void save(Path root, T config) throws IOException {
        var path = resolve(root);

        save.getOrDefault(State.PRE, Collections.emptySet()).forEach(listener -> listener.accept(config));

        Files.createDirectories(path.getParent());
        byte[] cfg = GSON.toJson(config).getBytes();
        if (Files.exists(path)) {
            byte[] old = Files.readAllBytes(path);
            if (Arrays.equals(old, cfg)) {
                save.getOrDefault(State.POST, Collections.emptySet()).forEach(listener -> listener.accept(config));
                return;
            }
        }
        Files.write(path, cfg);
        save.getOrDefault(State.POST, Collections.emptySet()).forEach(listener -> listener.accept(config));
    }

    public Path resolve(Path root) {
        return root.resolve(name() + ".json");
    }

    @Override
    public ConfigManager<T> onSave(State state, Listener<T> listener) {
        save.computeIfAbsent(state, k -> new HashSet<>()).add(listener);
        return this;
    }

    @Override
    public ConfigManager<T> onLoad(Listener<T> listener) {
        load.add(listener);
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
