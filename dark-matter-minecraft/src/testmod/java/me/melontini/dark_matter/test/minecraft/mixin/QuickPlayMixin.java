package me.melontini.dark_matter.test.minecraft.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlay;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QuickPlay.class)
public class QuickPlayMixin {

    @Inject(at = @At("HEAD"), method = "startSingleplayer", cancellable = true)
    private static void dark_matter$bypassWorld(MinecraftClient client, String levelName, CallbackInfo ci) {
        if (!client.getLevelStorage().levelExists(levelName)) {
            client.createIntegratedServerLoader().createAndStart(levelName,
                    new LevelInfo("dm_test_world", GameMode.CREATIVE, false, Difficulty.EASY, true,
                            new GameRules(), DataConfiguration.SAFE_MODE),
                    new GeneratorOptions(0, true, false),
                    registryManager -> registryManager.get(RegistryKeys.WORLD_PRESET).entryOf(WorldPresets.FLAT).value().createDimensionsRegistryHolder(), null);
            ci.cancel();
        }
    }
}
