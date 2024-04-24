package me.melontini.dark_matter.api.crash_handler;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.Context;
import me.melontini.dark_matter.impl.crash_handler.CrashlyticsInternals;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

@UtilityClass
public final class Crashlytics {

    public static void addHandler(String id, Handler handler) {
        CrashlyticsInternals.addHandler(id, handler);
    }

    public static void removeHandler(String id) {
        CrashlyticsInternals.removeHandler(id);
    }

    public static boolean hasHandler(String id) {
        return CrashlyticsInternals.hasHandler(id);
    }

    /**
     * Optional keys that may be attached to a crash report.
     */
    public static class Keys {
        public static final Context.Key<CrashReport> CRASH_REPORT = Context.key("crash_report");
        public static final Context.Key<String> LATEST_LOG = Context.key("latest_log");
        public static final Context.Key<IMixinInfo> MIXIN_INFO = Context.key("mixin_info");
        public static final Context.Key<String> MIXIN_STAGE = Context.key("mixin_stage");
    }

    @FunctionalInterface
    public interface Handler {
        void handle(Throwable cause, Context context);
    }
}
