package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.OptionManager;

import java.lang.reflect.Field;
import java.nio.file.Path;

public interface ConfigManager<T> {

    T getConfig();
    T getDefaultConfig();

    <V> V get(String option) throws NoSuchFieldException;
    void set(String option, Object value) throws NoSuchFieldException;
    Field getField(String option) throws NoSuchFieldException;

    OptionManager<T> getOptionManager();

    Path getPath();
    void save();
}
