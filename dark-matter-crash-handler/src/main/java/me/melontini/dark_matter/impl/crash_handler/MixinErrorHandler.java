package me.melontini.dark_matter.impl.crash_handler;

import me.melontini.dark_matter.api.base.util.Context;
import me.melontini.dark_matter.api.crash_handler.Crashlytics;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinErrorHandler implements IMixinErrorHandler {

    @Override
    public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action) {
        if (action == ErrorAction.ERROR) {
            handle(mixin, th, "prepare");
        }
        return action;
    }

    @Override
    public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action) {
        if (action == ErrorAction.ERROR) {
            handle(mixin, th, "apply");
        }
        return action;
    }

    private static void handle(IMixinInfo mixin, Throwable th, String stage) {
        CrashlyticsInternals.handleCrash(th, Context.builder()
                .put(Crashlytics.Keys.MIXIN_INFO, mixin)
                .put(Crashlytics.Keys.MIXIN_STAGE, stage)
                .put(Crashlytics.Keys.LATEST_LOG, CrashlyticsInternals.tryReadLog())
                .build());
    }
}
