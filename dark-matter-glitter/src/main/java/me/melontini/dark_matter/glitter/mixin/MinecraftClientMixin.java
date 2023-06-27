package me.melontini.dark_matter.glitter.mixin;

import me.melontini.dark_matter.glitter.client.util.ScreenParticleHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Unique
    private boolean dark_matter$init = false;

    @Inject(method = "method_29338", at = @At("TAIL"))
    private void dark_matter$init(CallbackInfo ci) {
        this.dark_matter$init = true;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;tick(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/hit/HitResult;)V", shift = At.Shift.AFTER))
    private void dark_matter$tickParticles(CallbackInfo ci) {
        if (this.dark_matter$init) ScreenParticleHelper.tickParticles();
    }

    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;tick()V"))
    private void dark_matter$tickScreen(CallbackInfo ci) {
        if (this.dark_matter$init) ScreenParticleHelper.tickParticles();
    }
}
