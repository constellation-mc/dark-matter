package me.melontini.dark_matter.impl.minecraft.mixin.events;

import me.melontini.dark_matter.api.minecraft.client.events.AfterFirstReload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
  @Inject(method = "method_29338", at = @At("TAIL"), require = 0)
  private void dark_matter$init(CallbackInfo ci) {
    MinecraftClient.getInstance().send(() -> {
      try {
        AfterFirstReload.EVENT.invoker().afterFirstReload();
      } catch (Throwable t) {
        CrashReport report = CrashReport.create(t, "Running event");
        MinecraftClient.printCrashReport(report);
      }
    });
  }
}
