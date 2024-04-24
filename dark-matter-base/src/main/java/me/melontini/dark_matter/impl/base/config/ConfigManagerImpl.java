package me.melontini.dark_matter.impl.base.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.NonNull;
import me.melontini.dark_matter.api.base.config.ConfigManager;
import me.melontini.dark_matter.api.base.util.Context;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
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
    public ConfigManager<T> fixup(@NonNull Consumer<JsonObject> fixer) {
        fixers.add(fixer);
        return this;
    }

    @Override
    public T createDefault() {
        return this.constructor.get();
    }

    @Override
    public T load(Path root, @NonNull Context context) {
        var path = resolve(root);
        Gson gson = context.get(ConfigManager.GSON).orElse(GSON);

        T config;
        if (Files.exists(path)) {
            try (var reader = Files.newBufferedReader(path)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                fixers.forEach(fixer -> fixer.accept(json));
                config = gson.fromJson(json, type());
            } catch (Exception e) {
                handlers.forEach(handler -> handler.accept(e, Stage.LOAD, path));
                config = createDefault();
            }
        } else {
            config = createDefault();
        }
        var conf = Objects.requireNonNull(config);
        load.forEach(listener -> listener.accept(conf, path));
        return conf;
    }

    @Override
    public void save(Path root, T config, @NonNull Context context) {
        var path = resolve(root);
        Gson gson = context.get(ConfigManager.GSON).orElse(GSON);

        save.forEach(listener -> listener.accept(config, path));

        try {
            var parent = path.getParent();
            if (parent != null) Files.createDirectories(parent);
            byte[] cfg = gson.toJson(config).getBytes(StandardCharsets.UTF_8);
            if (Files.exists(path) && Arrays.equals(Files.readAllBytes(path), cfg)) {
                return;
            }
            Files.write(path, cfg);
        } catch (Exception e) {
            handlers.forEach(handler -> handler.accept(e, Stage.SAVE, path));
        }
    }

    @Override
    public Path resolve(@NonNull Path root) {
        return root.resolve(name() + ".json");
    }

    @Override
    public ConfigManager<T> onSave(@NonNull Listener<T> listener) {
        save.add(listener);
        return this;
    }

    @Override
    public ConfigManager<T> onLoad(@NonNull Listener<T> listener) {
        load.add(listener);
        return this;
    }

    @Override
    public ConfigManager<T> exceptionHandler(@NonNull Handler handler) {
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
