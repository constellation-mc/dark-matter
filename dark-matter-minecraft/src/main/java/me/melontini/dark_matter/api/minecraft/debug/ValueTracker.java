package me.melontini.dark_matter.api.minecraft.debug;

import me.melontini.dark_matter.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated(since = "0.4.0")
@SuppressWarnings("unused")
public class ValueTracker {
    private ValueTracker() {
        throw new UnsupportedOperationException();
    }
    public static final Map<Field, List<Object>> AUTO_TRACK = new HashMap<>();
    public static final List<Field> AUTO_TRACK_STATIC = new ArrayList<>();
    public static final Map<String, Object> MANUAL_TRACK = new HashMap<>();


    public static void addTracker(Object o, Field f) {
        ReflectionUtil.setAccessible(f);
        AUTO_TRACK.computeIfAbsent(f, field -> new ArrayList<>()).add(o);
    }

    public static void removeTracker(Object o, Field f) {
        if (AUTO_TRACK.containsKey(f)) {
            AUTO_TRACK.get(f).remove(o);
            if (AUTO_TRACK.get(f).isEmpty()) {
                AUTO_TRACK.remove(f);
            }
        }
    }

    public static void addStaticTracker(Field f) {
        ReflectionUtil.setAccessible(f);
        AUTO_TRACK_STATIC.add(f);
    }

    public static void removeStaticTracker(Field f) {
        AUTO_TRACK_STATIC.remove(f);
    }

    public static void manualTrack(String s, Object o) {
        MANUAL_TRACK.put(s, o);
    }

    public static void manualTrack(Class<?> c, Object o) {
        MANUAL_TRACK.put(c.getName(), o);
    }

    public static void removeManualTrack(String s) {
        MANUAL_TRACK.remove(s);
    }

    public static void removeManualTrack(Class<?> c) {
        MANUAL_TRACK.remove(c.getName());
    }
}
