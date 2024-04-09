package me.melontini.dark_matter.test.minecraft;

import me.melontini.dark_matter.api.base.util.EntrypointRunner;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import me.melontini.dark_matter.impl.minecraft.util.test.DarkMatterClientTest;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.MixinEnvironment;

import static me.melontini.dark_matter.impl.minecraft.util.test.FabricClientTestHelper.waitForWorldTicks;

public class ClientTestHelper implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
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
