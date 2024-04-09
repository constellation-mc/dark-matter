package me.melontini.dark_matter.test.minecraft.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
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

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "method_53528", at = @At("HEAD"), cancellable = true)
    private void avoidQuickPlayArg(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!client.getLevelStorage().levelExists("dm_test_world")) {
            client.createIntegratedServerLoader().createAndStart("dm_test_world",
                    new LevelInfo("dm_test_world", GameMode.CREATIVE, false, Difficulty.EASY, true,
                            new GameRules(), DataConfiguration.SAFE_MODE),
                    new GeneratorOptions(0, true, false),
                    registryManager -> registryManager.get(RegistryKeys.WORLD_PRESET).entryOf(WorldPresets.FLAT).value().createDimensionsRegistryHolder(), null);
        } else {
            client.createIntegratedServerLoader().start("dm_test_world", () -> client.setScreen(new TitleScreen()));
        }
        ci.cancel();
    }
}
