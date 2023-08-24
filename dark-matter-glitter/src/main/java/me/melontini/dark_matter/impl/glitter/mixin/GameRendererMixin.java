package me.melontini.dark_matter.impl.glitter.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.melontini.dark_matter.impl.glitter.ScreenParticleInternals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;world:Lnet/minecraft/client/world/ClientWorld;", ordinal = 1, shift = At.Shift.BY, by = -3), method = "render")
    private void dark_matter$setLocalMatrixStack(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Local(ordinal = 1) MatrixStack matrixStack2, @Share("matrixStack2") LocalRef<MatrixStack> matrixStackLocalRef) {
        matrixStackLocalRef.set(matrixStack2);
    }

    @Inject(at = @At("TAIL"), method = "render")
    private void dark_matter$renderScreenParticles(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Share("matrixStack2") LocalRef<MatrixStack> matrixStackLocalRef) {
        MatrixStack stack = matrixStackLocalRef.get();
        ScreenParticleInternals.renderParticles(this.client, stack != null ? stack : new MatrixStack());
    }

}
