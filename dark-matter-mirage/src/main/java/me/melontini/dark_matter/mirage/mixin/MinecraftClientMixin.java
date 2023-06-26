package me.melontini.dark_matter.mirage.mixin;

import me.melontini.dark_matter.mirage.FakeWorld;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {//TODO move this out of base
    @Inject(method = "method_29338", at = @At("TAIL"))
    private void dark_matter$init(CallbackInfo ci) {
        FakeWorld.init();
    }
}
