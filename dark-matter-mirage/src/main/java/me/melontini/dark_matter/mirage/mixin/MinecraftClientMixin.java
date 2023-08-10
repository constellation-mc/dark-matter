package me.melontini.dark_matter.mirage.mixin;

import me.melontini.dark_matter.mirage.FakeWorld;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "method_29338", at = @At("TAIL"), require = 0)
    private void dark_matter$init(CallbackInfo ci) {
        FakeWorld.init();
    }

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Inject(method = "lambda$new$4(Lcom/mojang/realmsclient/client/RealmsClient;Lnet/minecraft/server/packs/resources/ReloadInstance;Lnet/minecraft/client/main/GameConfig;)V", at = @At("TAIL"), require = 0)
    private void dark_matter$tryInitOnForge(CallbackInfo ci) {//I have no idea what I'm doing. This might be different every time. Why would forge do this...
        FakeWorld.init();
    }
}
