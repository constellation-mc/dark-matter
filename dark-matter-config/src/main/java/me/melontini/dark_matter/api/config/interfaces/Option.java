package me.melontini.dark_matter.api.config.interfaces;

import me.melontini.dark_matter.impl.config.FieldOption;

import java.lang.reflect.Field;

public interface Option {
    Object get(Object parent);
    void set(Object parent, Object value);
    String name();
    Class<?> type();

    static Option ofField(Field f) {
        return new FieldOption(f);
    }
}
