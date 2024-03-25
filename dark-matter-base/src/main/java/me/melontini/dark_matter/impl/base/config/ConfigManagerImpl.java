package me.melontini.dark_matter.impl.base.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.experimental.ExtensionMethod;
import me.melontini.dark_matter.api.base.config.ConfigManager;
import me.melontini.dark_matter.api.base.util.Context;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ExtensionMethod(Files.class)
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
    public T load(Path root, Context context) {
        var path = resolve(root);
        Gson gson = context.get(ConfigManager.GSON).orElse(GSON);

        AtomicReference<T> config = new AtomicReference<>();
        if (path.exists()) {
            try (var reader = path.newBufferedReader()) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                fixers.forEach(fixer -> fixer.accept(json));
                config.set(gson.fromJson(json, type()));
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
    public void save(Path root, T config, Context context) {
        var path = resolve(root);
        Gson gson = context.get(ConfigManager.GSON).orElse(GSON);

        save.forEach(listener -> listener.accept(config, path));

        try {
            path.getParent().createDirectories();
            byte[] cfg = gson.toJson(config).getBytes();
            if (path.exists() && Arrays.equals(path.readAllBytes(), cfg)) {
                return;
            }
            path.write(cfg);
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
