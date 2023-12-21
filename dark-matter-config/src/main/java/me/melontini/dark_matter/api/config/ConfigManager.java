package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Option;
import me.melontini.dark_matter.api.config.serializers.ConfigSerializer;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface ConfigManager<T> {

    T getConfig();
    T getDefaultConfig();
    T createDefault();

    /**
     * @throws NoSuchOptionException if the option is not found
     */
    <V> V get(String key);
    default <V> V get(Class<V> type, String key) {return get(key);}
    <V> V getDefault(String option);
    default <V> V getDefault(Class<V> type, String key) {return get(key);}
    void set(String key, Object value);
    default void resetToDefault(String... keys) {
        for (String key : keys) {
            set(key, getDefault(key));
        }
    }

    Collection<Option> getOptions();
    List<Option> getOptions(String option);
    default Option getOption(String option) {
        List<Option> fields = getOptions(option);
        return fields.get(fields.size() - 1);
    }
    String getKey(Option field);
    Collection<String> getKeys();

    OptionManager<T> getOptionManager();
    ConfigSerializer<T> getSerializer();

    ModContainer getMod();
    String getName();
    Class<T> getType();

    default void load() {
        load(true);
    }
    void load(boolean save);
    void save();

    void postLoad(Event<T> consumer);
    void postSave(Event<T> consumer);

    interface Event<T> extends Consumer<ConfigManager<T>> {
        @Override
        void accept(ConfigManager<T> manager);
    }
}
