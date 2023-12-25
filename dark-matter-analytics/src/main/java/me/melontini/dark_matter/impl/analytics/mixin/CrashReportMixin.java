package me.melontini.dark_matter.impl.analytics.mixin;

import me.melontini.dark_matter.api.analytics.crashes.Crashlytics;
import me.melontini.dark_matter.api.base.util.classes.Context;
import me.melontini.dark_matter.impl.analytics.crashes.CrashlyticsInternals;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {

    @Inject(at = @At("RETURN"), method = "writeToFile", require = 0)
    private void dark_matter$handleCrash(File file, CallbackInfoReturnable<Boolean> cir) {
        CrashlyticsInternals.handleCrash(((CrashReport)(Object)this).getCause(), Context.builder()
                .put(Crashlytics.Keys.CRASH_REPORT, this)
                .put(Crashlytics.Keys.LATEST_LOG, CrashlyticsInternals.tryReadLog())
                .build());
    }
}
