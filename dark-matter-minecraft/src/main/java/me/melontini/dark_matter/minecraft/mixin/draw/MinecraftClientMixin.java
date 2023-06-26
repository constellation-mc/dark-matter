package me.melontini.dark_matter.minecraft.mixin.draw;

import me.melontini.dark_matter.minecraft.client.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    private void dark_matter$beforeInit(CallbackInfo ci) {
        if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().getWindow() != null) DrawUtil.FAKE_SCREEN.reset(MinecraftClient.getInstance(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
    }
}
