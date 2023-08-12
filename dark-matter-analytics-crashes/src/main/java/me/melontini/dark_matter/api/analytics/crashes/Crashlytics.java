package me.melontini.dark_matter.api.analytics.crashes;

import me.melontini.dark_matter.impl.analytics.crashes.CrashlyticsInternals;
import net.fabricmc.api.EnvType;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.Nullable;

public class Crashlytics {

    private Crashlytics() {
        throw new UnsupportedOperationException();
    }

    public static void addHandler(String id, Decider decider, Handler handler) {
        CrashlyticsInternals.addHandler(id, decider, handler);
    }

    public static void removeHandler(String id) {
        CrashlyticsInternals.removeHandler(id);
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
