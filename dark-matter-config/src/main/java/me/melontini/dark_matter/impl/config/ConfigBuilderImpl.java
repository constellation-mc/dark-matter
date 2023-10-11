package me.melontini.dark_matter.impl.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.config.*;
import me.melontini.dark_matter.api.config.interfaces.ConfigClassScanner;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;
import net.fabricmc.loader.api.ModContainer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.melontini.dark_matter.api.base.util.MakeSure.notNull;
import static me.melontini.dark_matter.api.base.util.Utilities.cast;

public class ConfigBuilderImpl<T> implements ConfigBuilder<T> {

    private final String name;
    private final Class<T> cls;
    private final ModContainer mod;

    private Supplier<T> ctx;
    private Consumer<OptionProcessorRegistry<T>> registrar;
    private ConfigClassScanner scanner;
    private Gson gson;
    private Function<TextEntry.InfoHolder<T>, TextEntry> reasonFactory;

    private final FixupsBuilder fixups = FixupsBuilder.create();
    private final RedirectsBuilder redirects = RedirectsBuilder.create();

    private Getter<T> getter;
    private Setter<T> setter;

    public ConfigBuilderImpl(Class<T> cls, ModContainer mod, String name) {
        this.name = name;
        this.cls = cls;
        this.mod = mod;
    }

    @Override
    public ConfigBuilder<T> constructor(Supplier<T> ctx) {
        this.ctx = ctx;
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> fixups(Consumer<FixupsBuilder> fixups) {
        fixups.accept(this.fixups);
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> redirects(Consumer<RedirectsBuilder> redirects) {
        redirects.accept(this.redirects);
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> getter(Getter<T> getter) {
        this.getter = getter;
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> setter(Setter<T> setter) {
        this.setter = setter;
        return this;
    }

    public ConfigBuilderImpl<T> gson(Gson gson) {
        this.gson = gson;
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> processors(Consumer<OptionProcessorRegistry<T>> consumer) {
        this.registrar = consumer;
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> scanner(ConfigClassScanner scanner) {
        this.scanner = scanner;
        return this;
    }

    @Override
    public ConfigBuilderImpl<T> defaultReason(Function<TextEntry.InfoHolder<T>, TextEntry> reason) {
        this.reasonFactory = reason;
        return this;
    }

    @Override
    public ConfigManager<T> build() {
        return new ConfigManagerImpl<>(this.cls, this.mod, this.name, notNull(this.gson, this::defaultGson))
        .setupOptionManager(this.registrar, notNull(this.reasonFactory, this::defaultReason))
        .setAccessors(notNull(this.getter, this::defaultGetter), notNull(this.setter, this::defaultSetter))
        .setFixups(this.fixups)
        .setRedirects(this.redirects)
        .setScanner(this.scanner)
        .afterBuild(notNull(this.ctx, () -> defaultCtx(this.cls)));
    }

    private Function<TextEntry.InfoHolder<T>, TextEntry> defaultReason() {
        return (holder) -> TextEntry.translatable(holder.manager().getMod().getMetadata().getId() + ".config.option_manager.reason." + holder.processor());
    }

    private Supplier<T> defaultCtx(Class<T> cls) {
        return () -> {
            try {
                return Reflect.setAccessible(cls.getDeclaredConstructor()).newInstance();
            } catch (Throwable t) {
                throw new RuntimeException("Failed to construct config class", t);
            }
        };
    }

    private Gson defaultGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    private Getter<T> defaultGetter() {
        return (manager, option) -> {
            List<Field> fields = manager.getFields(option);
            Object obj = manager.getConfig();
            for (Field field : fields) {
                field.setAccessible(true);
                obj = field.get(obj);
            }
            return cast(obj);
        };
    }

    private Setter<T> defaultSetter() {
        return (manager, option, value) -> {
            List<Field> fields = manager.getFields(option);
            Object obj = manager.getConfig();
            for (int i = 0; i < fields.size() - 1; i++) {
                Field field = fields.get(i);
                field.setAccessible(true);
                obj = field.get(obj);
            }
            Field f = fields.get(fields.size() - 1);
            f.setAccessible(true);
            f.set(obj, value);
        };
    }
}
