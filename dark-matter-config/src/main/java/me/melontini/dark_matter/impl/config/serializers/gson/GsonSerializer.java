package me.melontini.dark_matter.impl.config.serializers.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import me.melontini.dark_matter.api.config.serializers.gson.Fixups;
import me.melontini.dark_matter.impl.base.DarkMatterLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

public class GsonSerializer<T> implements ConfigSerializer<T> {

    private final ConfigManager<T> manager;
    private final Gson gson;
    private Function<JsonObject, JsonObject> fixupFunc = Function.identity();

    public GsonSerializer(ConfigManager<T> manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    public GsonSerializer(ConfigManager<T> manager) {
        this(manager, new GsonBuilder().setPrettyPrinting().create());
    }

    public GsonSerializer<T> setFixups(Fixups fixups) {
        this.fixupFunc = fixups::fixup;
        return this;
    }

    @Override
    public T load(Path path) {
        path = this.getPath(path);
        if (Files.exists(path)) {
            try (var reader = Files.newBufferedReader(path)) {
                JsonObject object = this.fixupFunc.apply(JsonParser.parseReader(reader).getAsJsonObject());

                return this.gson.fromJson(object, this.getConfigManager().getType());
            } catch (IOException e) {
                DarkMatterLog.error("Failed to load {}, using defaults", path);
            }
        }
        return this.getConfigManager().createDefault();
    }

    @Override
    public void save(Path path, T config) {
        path = this.getPath(path);
        try {
            Files.createDirectories(path.getParent());
            byte[] cfg = this.gson.toJson(config).getBytes();
            if (Files.exists(path)) {
                byte[] current = Files.readAllBytes(path);
                if (Arrays.equals(cfg, current)) return;
            }
            Files.write(path, cfg);
        } catch (Exception e) {
            DarkMatterLog.error("Failed to save {}", path, e);
        }
    }

    @Override
    public Path getPath(Path path) {
        return path.resolve(getConfigManager().getName() + ".json");
    }

    @Override
    public ConfigManager<T> getConfigManager() {
        return this.manager;
    }
}
