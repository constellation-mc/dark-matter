package me.melontini.dark_matter.test.glitter;

import me.melontini.dark_matter.api.glitter.ScreenParticleHelper;
import me.melontini.dark_matter.impl.minecraft.util.test.DarkMatterClientTest;
import me.melontini.dark_matter.impl.minecraft.util.test.FabricClientTestHelper;
import net.minecraft.particle.ParticleTypes;

public class ScreenParticleHelperTest implements DarkMatterClientTest {
    @Override
    public void onDarkMatterClientTest() {
        for (int i = 0; i < 5; i++) {
            FabricClientTestHelper.submitAndWait(client -> {
                ScreenParticleHelper.addParticles(ParticleTypes.END_ROD, 40, 40, 0.7, 0.7, 0.07, 5);
                return null;
            });
            FabricClientTestHelper.waitForWorldTicks(1);
        }
        FabricClientTestHelper.takeScreenshot("glitter-vanilla");
    }
}
