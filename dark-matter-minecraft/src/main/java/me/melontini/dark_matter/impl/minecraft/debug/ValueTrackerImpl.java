package me.melontini.dark_matter.impl.minecraft.debug;

import me.melontini.dark_matter.api.base.reflect.ReflectionUtil;
import me.melontini.dark_matter.api.base.util.classes.Tuple;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

@ApiStatus.Internal
public class ValueTrackerImpl {

    private static final Map<String, Supplier<?>> TRACKERS = new HashMap<>();
    private static final Map<String, Supplier<?>> VIEW = Collections.unmodifiableMap(TRACKERS);

    private static final Map<String, Tuple<Instant, Duration>> TIMERS = new HashMap<>();
    private static final Set<String> FOR_REMOVAL = new HashSet<>();

    public static void removeTracker(String s) {
        TRACKERS.remove(s);
        TIMERS.remove(s);
    }

    public static void addTracker(String s, Supplier<?> supplier) {
        TRACKERS.putIfAbsent(s, supplier);
    }

    public static void addTracker(String s, Supplier<?> supplier, Duration duration) {
        TRACKERS.putIfAbsent(s, supplier);
        TIMERS.put(s, Tuple.of(Instant.now(), duration));
    }

    public static void addFieldTracker(String s, Field f, Object o) {
        ReflectionUtil.setAccessible(f);
        TRACKERS.put(s, () -> {
            try {
                return f.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void addFieldTracker(String s, Field f, Object o, Duration duration) {
        ReflectionUtil.setAccessible(f);
        TRACKERS.put(s, () -> {
            try {
                return f.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        TIMERS.put(s, Tuple.of(Instant.now(), duration));
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

    public static void addStaticFieldTracker(String s, Field f, Duration duration) {
        ReflectionUtil.setAccessible(f);
        TRACKERS.putIfAbsent(s, () -> {
            try {
                return f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        TIMERS.put(s, Tuple.of(Instant.now(), duration));
    }

    public static void checkTimers() {
        Instant now = Instant.now();
        TIMERS.forEach((s, tuple) -> {
            if (tuple.left().plus(tuple.right()).isBefore(now)) {
                FOR_REMOVAL.add(s);
            }
        });
        FOR_REMOVAL.forEach(s -> {
            TIMERS.remove(s);
            TRACKERS.remove(s);
        });
        FOR_REMOVAL.clear();
    }

    public static Map<String, Supplier<?>> getView() {
        return VIEW;
    }

}
