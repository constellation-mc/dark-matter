package me.melontini.dark_matter.impl.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import me.melontini.dark_matter.api.base.util.EntrypointRunner;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import me.melontini.dark_matter.api.config.*;
import me.melontini.dark_matter.api.config.interfaces.ConfigClassScanner;
import me.melontini.dark_matter.api.config.interfaces.Option;
import me.melontini.dark_matter.api.config.interfaces.Redirects;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

public class ConfigManagerImpl<T> implements ConfigManager<T> {

    private final Class<T> configClass;
    private final ConfigRef<T> config = new ConfigRef<>();
    private Lazy<T> defaultConfig;

    private final ModContainer mod;
    private final String name;

    private Function<String, String> redirects;

    private ConfigBuilder.Getter<T> getter;
    private ConfigBuilder.Setter<T> setter;

    private OptionManagerImpl<T> optionManager;
    private ConfigSerializer<T> serializer;
    private Supplier<T> ctx;

    private final Map<Option, String> optionToKey = new ConcurrentHashMap<>();
    private final Map<String, List<FieldOption>> keyToOption = Collections.synchronizedMap(new LinkedHashMap<>());

    private final Set<ConfigClassScanner> scanners = Collections.synchronizedSet(new LinkedHashSet<>());

    private final Set<Consumer<ConfigManager<T>>> saveListeners = new HashSet<>();
    private final Set<Consumer<ConfigManager<T>>> loadListeners = new HashSet<>();

    public ConfigManagerImpl(Class<T> cls, ModContainer mod, String name) {
        this.configClass = cls;
        this.name = name;
        this.mod = mod;
    }

    ConfigManagerImpl<T> setupOptionManager(Set<BiConsumer<OptionProcessorRegistry<T>, ModContainer>> registrars, Function<TextEntry.InfoHolder<T>, TextEntry> defaultReason) {
        this.optionManager = new OptionManagerImpl<>(this, defaultReason);
        if (!registrars.isEmpty()) {
            for (var registrar : registrars) {
                registrar.accept(this.optionManager, this.getMod());
            }
        }
        EntrypointRunner.runWithContext(getShareId("processors"), BiConsumer.class, (consumer, mod) -> Utilities.consume(this.optionManager, mod, cast(consumer)));
        return this;
    }

    ConfigManagerImpl<T> setRedirects(RedirectsBuilder builder) {
        Redirects redirects = builder.build();
        this.redirects = redirects.isEmpty() ? Function.identity() : redirects::redirect;
        return this;
    }

    ConfigManagerImpl<T> setAccessors(ConfigBuilder.Getter<T> getter, ConfigBuilder.Setter<T> setter) {
        this.getter = getter;
        this.setter = setter;
        return this;
    }

    ConfigManagerImpl<T> setScanners(Set<ConfigClassScanner> scanners) {
        this.scanners.addAll(scanners);
        EntrypointRunner.run(getShareId("scanner"), Supplier.class, supplier -> this.scanners.add(cast(supplier.get())));
        this.scanners.removeIf(Objects::isNull);

        iterate(this.getType(), "", new HashSet<>(Arrays.asList(this.getType().getClasses())), new ArrayList<>());
        return this;
    }

    ConfigManagerImpl<T> addListeners(Set<Consumer<ConfigManager<T>>> saveListeners, Set<Consumer<ConfigManager<T>>> loadListeners) {
        this.loadListeners.addAll(loadListeners);
        this.saveListeners.addAll(saveListeners);
        return this;
    }

    ConfigManagerImpl<T> afterBuild(boolean save, Function<ConfigManager<T>, ConfigSerializer<T>> serializer, Supplier<T> ctx) {
        this.ctx = ctx;
        this.serializer = serializer.apply(this);

        this.load(save);
        this.defaultConfig = Lazy.of(() -> this::createDefault);
        return this;
    }

    private void iterate(Class<?> cls, String parentString, Set<Class<?>> recursive, List<Field> fieldRef) {
        for (Field field : cls.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            fieldRef.add(field);
            ImmutableList<Field> fieldRefView = ImmutableList.copyOf(fieldRef);
            keyToOption.putIfAbsent(parentString + field.getName(), fieldRefView.stream().map(FieldOption::new).toList());
            optionToKey.putIfAbsent(new FieldOption(field), parentString + field.getName());

            if (!this.scanners.isEmpty()) {
                ImmutableSet<Class<?>> classes = ImmutableSet.copyOf(recursive);
                scanners.forEach(scanner -> scanner.scan(cls, field, parentString, classes, fieldRefView));
            }
            if (recursive.contains(field.getType())) {
                recursive.addAll(Arrays.asList(field.getType().getClasses()));
                iterate(field.getType(), parentString + field.getName() + ".", recursive, fieldRef);
            }
            fieldRef.remove(field);
        }
    }

    @Override
    public void load(boolean save) {
        this.config.set(this.getSerializer().load(FabricLoader.getInstance().getConfigDir()));
        this.loadListeners.forEach(consumer -> consumer.accept(this));
        if (save) this.save();
    }

    @Override
    public T getConfig() {
        return this.config.get();
    }

    @Override
    public T getDefaultConfig() {
        return this.defaultConfig.get();
    }

    @Override
    public T createDefault() {
        return this.ctx.get();
    }

    @Override
    public <V> V get(String option) {
        return cast(this.getter.get(new ConfigBuilder.AccessorContext<>(this, getConfig()), this.redirects.apply(option)));
    }

    @Override
    public <V> V getDefault(String option) {
        return cast(this.getter.get(new ConfigBuilder.AccessorContext<>(this, getDefaultConfig()), this.redirects.apply(option)));
    }

    @Override
    public List<Option> getOptions(String option) {
        List<FieldOption> f = this.keyToOption.get(option = this.redirects.apply(option));
        if (f == null) throw new NoSuchOptionException(option);
        return Collections.unmodifiableList(f);
    }

    @Override
    public String getKey(Option field) {
        return this.optionToKey.get(field);
    }

    @Override
    public List<String> getKeys() {
        return this.keyToOption.keySet().stream().toList();
    }

    @Override
    public OptionManager<T> getOptionManager() {
        return this.optionManager;
    }

    @Override
    public ConfigSerializer<T> getSerializer() {
        return this.serializer;
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
    public void set(String option, Object value) {
        this.setter.set(new ConfigBuilder.AccessorContext<>(this, getConfig()), this.redirects.apply(option), value);
    }

    @Override
    public Collection<Option> getOptions() {
        return optionToKey.keySet();
    }

    @Override
    public Class<T> getType() {
        return this.configClass;
    }

    @Override
    public void save() {
        this.getOptionManager().processOptions();
        this.getSerializer().save(FabricLoader.getInstance().getConfigDir(), this.getConfig());

        this.saveListeners.forEach(consumer -> consumer.accept(this));
    }

    @Override
    public void postLoad(Event<T> consumer) {
        this.loadListeners.add(consumer);
    }

    @Override
    public void postSave(Event<T> consumer) {
        this.saveListeners.add(consumer);
    }

    private String getShareId(String key) {
        return this.getMod().getMetadata().getId() + ":config/" + this.getName() + "/" + key;
    }

    private static class ConfigRef<T> {

        private volatile T value;

        private T get() {
            return this.value;
        }

        private void set(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(this.value);
        }
    }
}
