package me.melontini.dark_matter.impl.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import me.melontini.dark_matter.api.base.util.EntrypointRunner;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.config.ConfigBuilder;
import me.melontini.dark_matter.api.config.ConfigManager;
import me.melontini.dark_matter.api.config.OptionManager;
import me.melontini.dark_matter.api.config.OptionProcessorRegistry;
import me.melontini.dark_matter.api.config.interfaces.Fixups;
import me.melontini.dark_matter.api.config.interfaces.Redirects;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigManagerImpl<T> implements ConfigManager<T> {

    final Class<T> configClass;
    T config;
    T defaultConfig;

    final Path configPath;
    final Gson gson;
    final ModContainer mod;
    final String name;

    private Function<JsonObject, JsonObject> fixupFunc;
    private Function<String, String> redirectFunc;

    private ConfigBuilder.Getter<T> getter;
    private ConfigBuilder.Setter<T> setter;

    private final OptionManagerImpl<T> optionManager;

    private final Map<Field, String> fieldToOption = new HashMap<>();
    private final Map<String, Field> optionToField = new HashMap<>();

    public ConfigManagerImpl(Class<T> cls, ModContainer mod, String name, Gson gson, @Nullable Consumer<OptionProcessorRegistry<T>> registrar) {
        this.configClass = cls;
        this.name = name;
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(name + ".json");
        this.gson = gson;
        this.mod = mod;

        this.optionManager = new OptionManagerImpl<>(this);
        if (registrar != null) registrar.accept(this.optionManager);
        EntrypointRunner.runEntrypoint(mod.getMetadata().getId() + ":config-processors-" + name, Consumer.class, consumer -> ((Consumer<OptionManager>)consumer).accept(this.optionManager));
    }

    void setFixups(Fixups fixups) {
        this.fixupFunc = fixups != null ? fixups::fixup : Function.identity();
    }

    void setRedirects(Redirects redirects) {
        this.redirectFunc = redirects != null ? redirects::redirect : Function.identity();
    }

    void setAccessors(ConfigBuilder.Getter<T> getter, ConfigBuilder.Setter<T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    void afterBuild(@Nullable Supplier<T> supplier) {
        if (supplier == null) supplier = () -> {
            try {
                Constructor<T> ctx = this.configClass.getDeclaredConstructor();
                ctx.setAccessible(true);
                return ctx.newInstance();
            } catch (Throwable t) {
                DarkMatterLog.error("Failed to construct config class", t);
                return null;
            }
        };

        this.load(supplier);
        this.defaultConfig = supplier.get();
    }

    @SneakyThrows
    private void iterate(Class<?> cls, Object parent, String parentString, Set<Class<?>> recursive) {
        for (Field declaredField : cls.getDeclaredFields()) {
            optionToField.putIfAbsent(parentString + declaredField.getName(), declaredField);
            fieldToOption.putIfAbsent(declaredField, parentString + declaredField.getName());

            if (recursive.contains(declaredField.getType())) {
                declaredField.setAccessible(true);
                recursive.addAll(Arrays.asList(declaredField.getType().getClasses()));
                iterate(declaredField.getType(), declaredField.get(parent), parentString + declaredField.getName() + ".", recursive);
            }
        }
    }

    private void load(Supplier<T> ctx) {
        if (Files.exists(this.configPath)) {
            try (var reader = Files.newBufferedReader(this.configPath)) {
                JsonObject object = this.fixupFunc.apply(JsonParser.parseReader(reader).getAsJsonObject());

                this.config = this.gson.fromJson(object, this.configClass);
                Set<Class<?>> recursive = new HashSet<>(Arrays.asList(this.configClass.getClasses()));
                iterate(this.configClass, this.config, "", recursive);
                this.save();
                return;
            } catch (IOException e) {
                DarkMatterLog.error("Failed to load {}, using defaults", this.configPath);
            }
        }
        this.config = ctx.get();
        Set<Class<?>> recursive = new HashSet<>(Arrays.asList(this.configClass.getClasses()));
        iterate(this.configClass, this.config, "", recursive);
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
            return Utilities.cast(this.getter.get(this, this.redirectFunc.apply(option)));
        } catch (IllegalAccessException t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public Field getField(String option) throws NoSuchFieldException {
        Field f = this.optionToField.get(option = this.redirectFunc.apply(option));
        if (f == null) throw new NoSuchFieldException(option);
        f.setAccessible(true);
        return f;
    }

    @Override
    public String getOption(Field field) {
        return this.fieldToOption.get(field);
    }

    @Override
    public OptionManager<T> getOptionManager() {
        return this.optionManager;
    }

    @Override
    public ModContainer getMod() {
        return this.mod;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void set(String option, Object value) throws NoSuchFieldException {
        try {
            this.setter.set(this, this.redirectFunc.apply(option), value);
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
            this.optionManager.processOptions();
            Files.write(this.configPath, this.gson.toJson(this.config).getBytes());
        } catch (Exception e) {
            DarkMatterLog.error("Failed to save {}", this.configPath, e);
        }
    }
}
