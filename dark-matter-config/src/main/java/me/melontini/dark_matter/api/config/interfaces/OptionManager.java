package me.melontini.dark_matter.api.config.interfaces;

import java.lang.reflect.Field;

public interface OptionManager<T> {

    boolean isModified(Field f);
    boolean isModified(String option) throws NoSuchFieldException;
}
