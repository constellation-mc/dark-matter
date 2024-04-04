package me.melontini.dark_matter.test.minecraft.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
    private void beforeInitScreen(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if ((Object) this instanceof DisconnectedScreen) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> System.exit(-1), 5, TimeUnit.SECONDS);
        }
    }
}
