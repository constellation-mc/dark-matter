package me.melontini.dark_matter.test.minecraft.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlay;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "method_53528", at = @At("HEAD"), cancellable = true)
    private void avoidQuickPlayArg(CallbackInfo ci) {
        QuickPlay.startQuickPlay(MinecraftClient.getInstance(), new RunArgs.QuickPlay("", "dm_test_world", "", ""), null);
        ci.cancel();
    }
}
