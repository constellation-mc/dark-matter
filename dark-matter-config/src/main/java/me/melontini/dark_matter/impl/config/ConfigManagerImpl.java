package me.melontini.dark_matter.impl.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.melontini.dark_matter.api.base.util.EntrypointRunner;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.config.*;
import me.melontini.dark_matter.api.config.interfaces.ConfigClassScanner;
import me.melontini.dark_matter.api.config.interfaces.Fixups;
import me.melontini.dark_matter.api.config.interfaces.Redirects;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

public class ConfigManagerImpl<T> implements ConfigManager<T> {

    final Class<T> configClass;
    final AtomicReference<T> config = new AtomicReference<>();
    T defaultConfig;
    Supplier<T> constructor;

    final Path configPath;
    final Gson gson;
    final ModContainer mod;
    final String name;

    private Function<JsonObject, JsonObject> fixupFunc;
    private Function<String, String> redirectFunc;

    private ConfigBuilder.Getter<T> getter;
    private ConfigBuilder.Setter<T> setter;

    private OptionManagerImpl<T> optionManager;

    private final Map<Field, String> fieldToOption = new HashMap<>();
    private final Map<String, List<Field>> optionToFields = new LinkedHashMap<>();

    private final Set<ConfigClassScanner> scanners = new LinkedHashSet<>();

    public ConfigManagerImpl(Class<T> cls, ModContainer mod, String name, Gson gson) {
        this.configClass = cls;
        this.name = name;
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(name + ".json");
        this.gson = gson;
        this.mod = mod;
    }

    void setupOptionManager(@Nullable Consumer<OptionProcessorRegistry<T>> registrar, Function<TextEntry.InfoHolder<T>, TextEntry> defaultReason) {
        this.optionManager = new OptionManagerImpl<>(this, defaultReason);
        if (registrar != null) registrar.accept(this.optionManager);
        EntrypointRunner.runEntrypoint(getShareId("processors"), Consumer.class, consumer -> Utilities.consume(this.optionManager, cast(consumer)));
    }

    void setFixups(FixupsBuilder builder) {
        EntrypointRunner.run(getShareId("fixups"), Consumer.class, consumer -> Utilities.consume(builder, cast(consumer)));

        Fixups fixups = builder.build();
        this.fixupFunc = fixups.isEmpty() ? Function.identity() : fixups::fixup;
    }

    void setRedirects(RedirectsBuilder builder) {
        EntrypointRunner.run(getShareId("redirects"), Consumer.class, consumer -> Utilities.consume(builder, cast(consumer)));

        Redirects redirects = builder.build();
        this.redirectFunc = redirects.isEmpty() ? Function.identity() : redirects::redirect;
    }

    void setAccessors(ConfigBuilder.Getter<T> getter, ConfigBuilder.Setter<T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    void setScanner(ConfigClassScanner scanner) {
        this.scanners.add(scanner);
        EntrypointRunner.run(getShareId("scanner"), Supplier.class, supplier -> this.scanners.add(cast(supplier.get())));
        this.scanners.removeIf(Objects::isNull);
        startScan();
    }

    void afterBuild(@Nullable Supplier<T> supplier) {
        if ((this.constructor = supplier) == null) this.constructor = () -> {
            try {
                Constructor<T> ctx = this.configClass.getDeclaredConstructor();
                ctx.setAccessible(true);
                return ctx.newInstance();
            } catch (Throwable t) {
                throw new RuntimeException("Failed to construct config class", t);
            }
        };

        this.load();
        this.defaultConfig = this.constructor.get();
    }

    private void iterate(Class<?> cls, String parentString, Set<Class<?>> recursive, List<Field> fieldRef) {
        for (Field declaredField : cls.getDeclaredFields()) {
            if (Modifier.isStatic(declaredField.getModifiers())) continue;

            fieldRef.add(declaredField);
            ImmutableList<Field> fieldRefView = ImmutableList.copyOf(fieldRef);
            optionToFields.putIfAbsent(parentString + declaredField.getName(), fieldRefView);
            fieldToOption.putIfAbsent(declaredField, parentString + declaredField.getName());

            if (!this.scanners.isEmpty()) {
                ImmutableSet<Class<?>> classes = ImmutableSet.copyOf(recursive);
                scanners.forEach(scanner -> scanner.scan(cls, declaredField, parentString, classes, fieldRefView));
            }
            if (recursive.contains(declaredField.getType())) {
                recursive.addAll(Arrays.asList(declaredField.getType().getClasses()));
                iterate(declaredField.getType(), parentString + declaredField.getName() + ".", recursive, fieldRef);
            }
            fieldRef.remove(declaredField);
        }
    }

    @Override
    public void load() {
        if (Files.exists(this.configPath)) {
            try (var reader = Files.newBufferedReader(this.configPath)) {
                JsonObject object = this.fixupFunc.apply(JsonParser.parseReader(reader).getAsJsonObject());

                this.config.set(this.gson.fromJson(object, this.configClass));
                this.save();
                return;
            } catch (IOException e) {
                DarkMatterLog.error("Failed to load {}, using defaults", this.configPath);
            }
        }
        this.config.set(this.constructor.get());
        this.save();
    }

    private void startScan() {
        iterate(this.configClass, "", new HashSet<>(Arrays.asList(this.configClass.getClasses())), new ArrayList<>());
    }

    @Override
    public T getConfig() {
        return this.config.get();
    }

    @Override
    public AtomicReference<T> getConfigRef() {
        return this.config;
    }

    @Override
    public T getDefaultConfig() {
        return this.defaultConfig;
    }

    @Override
    public <V> V get(String option) throws NoSuchFieldException {
        try {
            return cast(this.getter.get(this, this.redirectFunc.apply(option)));
        } catch (IllegalAccessException t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    public List<Field> getFields(String option) throws NoSuchFieldException {
        List<Field> f = this.optionToFields.get(option = this.redirectFunc.apply(option));
        if (f == null) throw new NoSuchFieldException(option);
        return f;
    }

    @Override
    public String getOption(Field field) {
        return this.fieldToOption.get(field);
    }

    @Override
    public List<String> getOptions() {
        return this.optionToFields.keySet().stream().toList();
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
            Files.createDirectories(this.configPath.getParent());
            Files.write(this.configPath, this.gson.toJson(this.config).getBytes());
        } catch (Exception e) {
            DarkMatterLog.error("Failed to save {}", this.configPath, e);
        }
    }

    private String getShareId(String key) {
        return this.mod.getMetadata().getId() + ":config-" + key + "-" + this.name;
    }
}
