package me.melontini.dark_matter.api.config.interfaces;

import me.melontini.dark_matter.api.base.util.classes.Tuple;

import java.lang.reflect.Field;
import java.util.Set;

public interface OptionManager<T> {

    boolean isModified(Field f);
    boolean isModified(String option) throws NoSuchFieldException;

    void processFeatures();

    Tuple<String, Set<String>> blameProcessors(Field f);
    Set<String> blameProcessors(String option) throws NoSuchFieldException;
    Tuple<String, Set<String>> blameMods(Field f);
    Set<String> blameMods(String option) throws NoSuchFieldException;
}
