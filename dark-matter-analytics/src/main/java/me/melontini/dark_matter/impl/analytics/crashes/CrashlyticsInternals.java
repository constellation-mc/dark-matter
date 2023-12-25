package me.melontini.dark_matter.impl.analytics.crashes;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.api.analytics.crashes.Crashlytics;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.classes.Context;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

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

    public static void init() {
        try {
            Class<?> cls = Class.forName("org.spongepowered.asm.mixin.Mixins");
            MethodHandle handle = MethodHandles.publicLookup().findStatic(cls, "registerErrorHandlerClass", MethodType.methodType(void.class, String.class));
            handle.invoke("me.melontini.dark_matter.impl.analytics.crashes.MixinErrorHandler");
        } catch (Throwable e) {
            DarkMatterLog.error("Failed to register mixin error handler!", e);
        }
    }

    public static String tryReadLog() {
        String latestLog = null;
        try {
            latestLog = Files.readString(FabricLoader.getInstance().getGameDir().resolve("logs/latest.log"));
        } catch (IOException ignored) {
        }
        return latestLog;
    }

    public static void handleCrash(Throwable cause, Context context) {
        if (Analytics.handleCrashes()) {
            for (Crashlytics.Handler handler : HANDLERS.values()) {
                handler.handle(cause, context);
            }
        }
    }
}
