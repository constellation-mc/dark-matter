package me.melontini.dark_matter.impl.glitter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.dark_matter.impl.glitter.ScreenParticleInternals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.BEFORE))
    private void dark_matter$renderScreenParticles(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Local DrawContext context) {
        this.client.getProfiler().push("dark_matter_particles");
        ScreenParticleInternals.renderParticles(this.client, context);
        this.client.getProfiler().pop();
    }
}
