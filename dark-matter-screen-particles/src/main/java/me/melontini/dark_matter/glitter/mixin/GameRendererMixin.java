package me.melontini.dark_matter.glitter.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.melontini.dark_matter.glitter.client.util.ScreenParticleHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
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

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;world:Lnet/minecraft/client/world/ClientWorld;", ordinal = 1, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT, method = "render")
    private void dark_matter$setLocalMatrixStack(float tickDelta, long startTime, boolean tick, CallbackInfo ci, int i, int j, Window window, Matrix4f matrix4f, MatrixStack matrixStack, MatrixStack matrixStack2, @Share("matrixStack2") LocalRef<MatrixStack> matrixStackLocalRef) {
        matrixStackLocalRef.set(matrixStack2);
    }

    @Inject(at = @At("TAIL"), method = "render")
    private void dark_matter$renderScreenParticles(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Share("matrixStack2") LocalRef<MatrixStack> matrixStackLocalRef) {
        MatrixStack stack = matrixStackLocalRef.get();
        ScreenParticleHelper.renderParticles(this.client, stack != null ? stack : new MatrixStack());
    }
}
