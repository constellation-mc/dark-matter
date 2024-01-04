package me.melontini.dark_matter.impl.crash_handler;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.classes.Context;
import me.melontini.dark_matter.api.crash_handler.Crashlytics;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

@UtilityClass
public final class CrashlyticsInternals {

    private static final Map<String, Crashlytics.Handler> HANDLERS = new HashMap<>();

    public static void addHandler(String id, Crashlytics.Handler handler) {
        MakeSure.notEmpty(id, "Empty or null id provided!");
        MakeSure.notNulls("Null arguments provided!", handler);
        HANDLERS.putIfAbsent(id, handler);
    }

    public static void removeHandler(String id) {
        MakeSure.notEmpty(id, "Empty or null id provided!");
        HANDLERS.remove(id);
    }

    public static boolean hasHandler(String id) {
        return HANDLERS.containsKey(id);
    }

    public static String tryReadLog() {
        String latestLog = null;
        try {
            latestLog = Files.readString(FabricLoader.getInstance().getGameDir().resolve("logs/latest.log"));
        } catch (IOException ignored) {
        }
        return latestLog;
    }

    private static final WeakHashMap<Throwable, Object> CAUGHT_EXCEPTIONS = new WeakHashMap<>();

    public static void handleCrash(Throwable cause, Context context) {
        if (CAUGHT_EXCEPTIONS.put(cause, new Object()) == null) {
            for (Crashlytics.Handler handler : HANDLERS.values()) {
                handler.handle(cause, context);
            }
        }
    }
}
