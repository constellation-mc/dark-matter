package me.melontini.dark_matter.api.analytics.crashes;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.classes.Context;
import me.melontini.dark_matter.impl.analytics.crashes.CrashlyticsInternals;

@UtilityClass
public final class Crashlytics {

    public static void addHandler(String id, Handler handler) {
        CrashlyticsInternals.addHandler(id, handler);
    }

    public static void removeHandler(String id) {
        CrashlyticsInternals.removeHandler(id);
    }

    public static class Keys {
        public static String CRASH_REPORT = "crash_report";
        public static String LATEST_LOG = "latest_log";
        public static String MIXIN_INFO = "mixin_info";
        public static String MIXIN_STAGE = "mixin_stage";
    }

    @FunctionalInterface
    public interface Handler {
        void handle(Throwable cause, Context context);
    }
}
