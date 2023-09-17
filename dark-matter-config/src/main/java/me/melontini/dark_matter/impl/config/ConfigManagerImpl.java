package me.melontini.dark_matter.impl.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.config.ConfigBuilder;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.interfaces.Fixups;
import me.melontini.dark_matter.api.config.interfaces.OptionManager;
import me.melontini.dark_matter.api.config.interfaces.Redirects;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class ConfigManagerImpl<T> implements ConfigManager<T> {

    T config;
    final T defaultConfig;

    final Path configPath;
    final Gson gson;
    final ModContainer mod;
    final String name;

    private final Function<JsonObject, JsonObject> fixupFunc;
    private final Function<String, String> redirectFunc;

    private final ConfigBuilder.Getter<T> getter;
    private final ConfigBuilder.Setter<T> setter;

    private final OptionManagerImpl<T> optionManager;

    @SneakyThrows
    public ConfigManagerImpl(Class<T> cls, ModContainer mod, String name, Gson gson, @Nullable Fixups fixups, @Nullable Redirects redirects, ConfigBuilder.Getter<T> getter, ConfigBuilder.Setter<T> setter) {
        Constructor<T> ctx = cls.getDeclaredConstructor();
        ctx.setAccessible(true);
        this.name = name;
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(name + ".json");
        this.gson = gson;
        this.mod = mod;

        this.fixupFunc = fixups != null ? fixups::fixup : Function.identity();
        this.redirectFunc = redirects != null ? redirects::redirect : Function.identity();

        this.getter = getter;
        this.setter = setter;

        this.optionManager = new OptionManagerImpl<>(this);
        FabricLoader.getInstance().getObjectShare().put(mod.getMetadata().getId() + ":config-processors-" + name, this.optionManager);
        this.load(ctx);
        this.defaultConfig = ctx.newInstance();
    }

    @SneakyThrows
    private void load(Constructor<T> ctx) {
        if (Files.exists(this.configPath)) {
            try (var reader = Files.newBufferedReader(this.configPath)) {
                JsonObject object = this.fixupFunc.apply(JsonParser.parseReader(reader).getAsJsonObject());

                this.config = this.gson.fromJson(object, ctx.getDeclaringClass());
                this.save();
                return;
            } catch (Exception e) {
                DarkMatterLog.error("Failed to load {}, using defaults", this.configPath);
            }
        }
        this.config = ctx.newInstance();
        this.save();
    }

    @Override
    public T getConfig() {
        return this.config;
    }

    @Override
    public T getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public <V> V get(String option) throws NoSuchFieldException {
        try {
            option = this.redirectFunc.apply(option);
            return Utilities.cast(this.getter.get(this, option));
        } catch (IllegalAccessException t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public Field getField(String option) throws NoSuchFieldException {
        try {
            option = this.redirectFunc.apply(option);
            Object obj = this.getConfig();
            String[] split = option.split("\\.");
            for (int i = 0; i < split.length - 1; i++) {
                Field field = obj.getClass().getDeclaredField(split[i]);
                field.setAccessible(true);
                obj = field.get(obj);
            }
            Field f = obj.getClass().getDeclaredField(split[split.length - 1]);
            f.setAccessible(true);
            return f;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OptionManager<T> getOptionManager() {
        return this.optionManager;
    }

    @Override
    public void set(String option, Object value) throws NoSuchFieldException {
        try {
            option = this.redirectFunc.apply(option);
            this.setter.set(this, option, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path getPath() {
        return this.configPath;
    }

    @Override
    public void save() {
        try {
            this.optionManager.processFeatures();
            Files.write(this.configPath, this.gson.toJson(this.config).getBytes());
        } catch (Exception e) {
            DarkMatterLog.error("Failed to save {}", this.configPath, e);
        }
    }
}
