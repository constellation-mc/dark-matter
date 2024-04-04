package me.melontini.dark_matter.test.minecraft.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(at = @At("TAIL"), method = "onPlayerConnect")
    private void dark_matter$shutdownTest(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> System.exit(0), 7, TimeUnit.SECONDS);
    }
}
