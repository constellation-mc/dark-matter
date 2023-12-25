package me.melontini.dark_matter.impl.analytics.crashes;

import me.melontini.dark_matter.api.analytics.crashes.Crashlytics;
import me.melontini.dark_matter.api.base.util.classes.Context;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinErrorHandler implements IMixinErrorHandler {

    @Override
    public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action) {
        if (action == ErrorAction.ERROR) {
            CrashlyticsInternals.handleCrash(th, Context.builder()
                    .put(Crashlytics.Keys.MIXIN_INFO, mixin)
                    .put(Crashlytics.Keys.MIXIN_STAGE, "prepare")
                    .put(Crashlytics.Keys.LATEST_LOG, CrashlyticsInternals.tryReadLog())
                    .build());
        }
        return action;
    }

    @Override
    public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action) {
        if (action == ErrorAction.ERROR) {
            CrashlyticsInternals.handleCrash(th, Context.builder()
                    .put(Crashlytics.Keys.MIXIN_INFO, mixin)
                    .put(Crashlytics.Keys.MIXIN_STAGE, "apply")
                    .put(Crashlytics.Keys.LATEST_LOG, CrashlyticsInternals.tryReadLog())
                    .build());
        }
        return action;
    }
}
