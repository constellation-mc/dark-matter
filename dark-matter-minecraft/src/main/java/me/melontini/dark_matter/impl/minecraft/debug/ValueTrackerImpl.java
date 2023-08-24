package me.melontini.dark_matter.impl.minecraft.debug;

import me.melontini.dark_matter.api.base.reflect.ReflectionUtil;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@ApiStatus.Internal
public class ValueTrackerImpl {

    private static final Map<String, Supplier<?>> TRACKERS = new HashMap<>();
    private static final Map<String, Supplier<?>> VIEW = Collections.unmodifiableMap(TRACKERS);

    public static void addTracker(String s, Supplier<?> supplier) {
        TRACKERS.putIfAbsent(s, supplier);
    }

    public static void removeTracker(String s) {
        TRACKERS.remove(s);
    }

    public static void addFieldTracker(String s, Field f, Supplier<Object> objectSupplier) {
        ReflectionUtil.setAccessible(f);
        TRACKERS.putIfAbsent(s, () -> {
            try {
                return f.get(objectSupplier.get());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void addStaticFieldTracker(String s, Field f) {
        ReflectionUtil.setAccessible(f);
        TRACKERS.putIfAbsent(s, () -> {
            try {
                return f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Map<String, Supplier<?>> getView() {
        return VIEW;
    }

}
