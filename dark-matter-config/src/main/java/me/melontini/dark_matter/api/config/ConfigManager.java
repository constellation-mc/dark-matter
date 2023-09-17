package me.melontini.dark_matter.api.config;

import net.fabricmc.loader.api.ModContainer;

import java.lang.reflect.Field;
import java.nio.file.Path;

public interface ConfigManager<T> {

    T getConfig();
    T getDefaultConfig();

    <V> V get(String option) throws NoSuchFieldException;
    void set(String option, Object value) throws NoSuchFieldException;
    Field getField(String option) throws NoSuchFieldException;
    String getOption(Field field);

    OptionManager<T> getOptionManager();

    ModContainer getMod();
    String getName();
    Path getPath();

    void save();
}
