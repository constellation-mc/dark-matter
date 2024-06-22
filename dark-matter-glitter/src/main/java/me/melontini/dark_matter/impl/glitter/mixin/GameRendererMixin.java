package me.melontini.dark_matter.impl.glitter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.dark_matter.impl.glitter.ScreenParticleInternals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void dark_matter$renderScreenParticles(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, boolean bl, int i, int j, Window window, Matrix4f matrix4f, Matrix4fStack matrix4fStack, DrawContext drawContext, @Local DrawContext context) {
        this.client.getProfiler().push("dark_matter_particles");
        ScreenParticleInternals.renderParticles(this.client, context);
        this.client.getProfiler().pop();
    }
}
