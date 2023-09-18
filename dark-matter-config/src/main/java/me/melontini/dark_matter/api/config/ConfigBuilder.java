package me.melontini.dark_matter.api.config;

import com.google.gson.Gson;
import me.melontini.dark_matter.api.config.interfaces.ConfigClassScanner;
import me.melontini.dark_matter.impl.config.ConfigBuilderImpl;
import net.fabricmc.loader.api.ModContainer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConfigBuilder<T> {

    static <T> ConfigBuilder<T> create(Class<T> cls, ModContainer mod, String name) {
        return new ConfigBuilderImpl<>(cls, mod, name);
    }

    ConfigBuilder<T> constructor(Supplier<T> ctx);

    ConfigBuilder<T> fixups(Consumer<FixupsBuilder> fixups);

    ConfigBuilder<T> redirects(Consumer<RedirectsBuilder> redirects);

    ConfigBuilder<T> getter(Getter<T> getter);

    ConfigBuilder<T> setter(Setter<T> setter);

    ConfigBuilder<T> gson(Gson gson);

    ConfigBuilder<T> processors(Consumer<OptionProcessorRegistry<T>> consumer);

    ConfigBuilder<T> scanner(ConfigClassScanner scanner);

    ConfigManager<T> build();

    interface Getter<T> {
        Object get(ConfigManager<T> configManager, String option) throws NoSuchFieldException, IllegalAccessException;
    }

    interface Setter<T> {
        void set(ConfigManager<T> manager, String option, Object value) throws NoSuchFieldException, IllegalAccessException;
    }
}
