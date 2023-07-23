package me.melontini.dark_matter.analytics.crashes;

import me.melontini.dark_matter.util.MakeSure;
import me.melontini.dark_matter.util.classes.Tuple;
import net.fabricmc.api.EnvType;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Crashlytics {
    private static final Map<String, Tuple<Decider, Handler>> HANDLERS = new HashMap<>();
    public static void addHandler(String id, Decider decider, Handler handler) {
        MakeSure.notEmpty(id, "Empty or null id provided!");
        MakeSure.notNulls("Null arguments provided!", decider, handler);
        HANDLERS.putIfAbsent(id, new Tuple<>(decider, handler));
    }

    public static void removeHandler(String id) {
        MakeSure.notEmpty(id, "Empty or null id provided!");
        HANDLERS.remove(id);
    }

    public static Collection<Tuple<Decider, Handler>> getHandlers() {
        return Collections.unmodifiableCollection(HANDLERS.values());
    }

    @FunctionalInterface
    public interface Decider {
        boolean shouldHandle(CrashReport report, Throwable cause, @Nullable String latestLog, EnvType envType);
    }

    @FunctionalInterface
    public interface Handler {
        void handle(CrashReport report, Throwable cause, @Nullable String latestLog, EnvType envType);
    }
}
