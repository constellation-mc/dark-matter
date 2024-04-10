package me.melontini.dark_matter.test.minecraft;

import me.melontini.dark_matter.api.base.util.EntrypointRunner;
import me.melontini.dark_matter.api.minecraft.client.events.AfterFirstReload;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import me.melontini.dark_matter.impl.minecraft.util.test.DarkMatterClientTest;
import net.fabricmc.api.ClientModInitializer;
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
import org.spongepowered.asm.mixin.MixinEnvironment;

import static me.melontini.dark_matter.impl.minecraft.util.test.FabricClientTestHelper.waitForWorldTicks;

public class ClientTestHelper implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AfterFirstReload.EVENT.register(() -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (!client.getLevelStorage().levelExists("dm_test_world")) {
                client.createIntegratedServerLoader().createAndStart("dm_test_world",
                        new LevelInfo("dm_test_world", GameMode.CREATIVE, false, Difficulty.EASY, true,
                                new GameRules(), DataConfiguration.SAFE_MODE),
                        new GeneratorOptions(0, true, false),
                        registryManager -> registryManager.get(RegistryKeys.WORLD_PRESET).entryOf(WorldPresets.FLAT).value().createDimensionsRegistryHolder());
            } else {
                client.createIntegratedServerLoader().start(new TitleScreen(), "dm_test_world");
            }
        });

        var thread = new Thread(() -> {
            try {
                DarkMatterLog.info("Started client test.");
                waitForWorldTicks(200);

                EntrypointRunner.run("dark-matter:client_test", DarkMatterClientTest.class, DarkMatterClientTest::onDarkMatterClientTest);
                MixinEnvironment.getCurrentEnvironment().audit();

                MinecraftClient.getInstance().scheduleStop();
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        });
        thread.start();
    }
}
