package me.melontini.dark_matter.api.config;

import net.fabricmc.loader.api.ModContainer;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

public interface ConfigManager<T> {

    T getConfig();
    T getDefaultConfig();

    <V> V get(String option) throws NoSuchFieldException;
    void set(String option, Object value) throws NoSuchFieldException;
    List<Field> getFields(String option) throws NoSuchFieldException;
    default Field getField(String option) throws NoSuchFieldException {
        List<Field> fields = getFields(option);
        return fields.get(fields.size() - 1);
    }
    String getOption(Field field);

    OptionManager<T> getOptionManager();

    ModContainer getMod();
    String getName();
    Path getPath();

    void save();
}
