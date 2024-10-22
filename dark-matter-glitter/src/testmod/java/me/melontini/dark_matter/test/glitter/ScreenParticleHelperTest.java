package me.melontini.dark_matter.test.glitter;

import me.melontini.dark_matter.api.glitter.ScreenParticleHelper;
import me.melontini.handytests.client.ClientTestContext;
import me.melontini.handytests.client.ClientTestEntrypoint;
import net.minecraft.particle.ParticleTypes;

public class ScreenParticleHelperTest implements ClientTestEntrypoint {

  @Override
  public void onClientTest(ClientTestContext context) {
    context.submitAndWait(client -> {
      ScreenParticleHelper.addParticles(ParticleTypes.END_ROD, 40, 40, 0.7, 0.7, 0.07, 10);
      return null;
    });
    context.waitForWorldTicks(1);
    context.takeScreenshot("glitter-vanilla");
  }
}
