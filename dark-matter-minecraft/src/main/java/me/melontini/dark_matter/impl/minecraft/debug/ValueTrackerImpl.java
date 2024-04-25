package me.melontini.dark_matter.impl.minecraft.debug;

import com.google.common.base.Strings;
import lombok.NonNull;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

public class ValueTrackerImpl {

    private static final Map<String, Supplier<?>> TRACKERS = new LinkedHashMap<>();
    private static final Map<String, Supplier<?>> VIEW = Collections.unmodifiableMap(TRACKERS);

    private static final Map<String, Tuple<Instant, Duration>> TIMERS = new HashMap<>();
    private static final Set<String> FOR_REMOVAL = new HashSet<>();

    public static void removeTracker(@NonNull String trackerName) {
        TRACKERS.remove(trackerName);
        TIMERS.remove(trackerName);
    }

    public static void addTracker(@NonNull String trackerName, @NonNull Supplier<?> supplier) {
        TRACKERS.put(trackerName, supplier);
    }

    public static void addTracker(@NonNull String trackerName, @NonNull Supplier<?> supplier, @NonNull Duration duration) {
        TRACKERS.put(trackerName, supplier);
        TIMERS.put(trackerName, Tuple.of(Instant.now(), duration));
    }

    public static void addFieldTracker(@NonNull String trackerName, @NonNull Field f, @NonNull Object o) {
        Reflect.setAccessible(f);
        TRACKERS.put(trackerName, () -> {
            try {
                return f.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void addFieldTracker(@NonNull String trackerName, @NonNull Field f, @NonNull Object o, @NonNull Duration duration) {
        Reflect.setAccessible(f);
        TRACKERS.put(trackerName, () -> {
            try {
                return f.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        TIMERS.put(trackerName, Tuple.of(Instant.now(), duration));
    }

    public static void addStaticFieldTracker(@NonNull String trackerName, @NonNull Field f) {
        Reflect.setAccessible(f);
        TRACKERS.put(trackerName, () -> {
            try {
                return f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void addStaticFieldTracker(@NonNull String trackerName, @NonNull Field f, @NonNull Duration duration) {
        Reflect.setAccessible(f);
        TRACKERS.put(trackerName, () -> {
            try {
                return f.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        TIMERS.put(trackerName, Tuple.of(Instant.now(), duration));
    }

    public static void checkTimers() {
        if (TIMERS.isEmpty()) return;

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

    private static final List<String> DARK_MATTER$VALUES_TO_RENDER = new ArrayList<>();

    public static void tick() {
        DARK_MATTER$VALUES_TO_RENDER.clear();

        ValueTrackerImpl.checkTimers();
        ValueTrackerImpl.getView().forEach((id, supplier) -> DARK_MATTER$VALUES_TO_RENDER.add(id + ": " + supplier.get()));
    }

    @Environment(EnvType.CLIENT)
    public static class Renderer {

        public static void render(DrawContext context) {
            if (!DARK_MATTER$VALUES_TO_RENDER.isEmpty()) {
                var textRenderer = MinecraftClient.getInstance().textRenderer;
                for (int i = 0; i < DARK_MATTER$VALUES_TO_RENDER.size(); ++i) {
                    String string = DARK_MATTER$VALUES_TO_RENDER.get(i);
                    if (!Strings.isNullOrEmpty(string)) {
                        int k = textRenderer.getWidth(string);
                        int m = 2 + 9 * i;
                        context.fill(1, m - 1,  2 + k + 1, m + 9 - 1, -1873784752);
                        context.drawText(textRenderer, string, 2, m, 14737632, false);
                    }
                }
            }
        }
    }
}
