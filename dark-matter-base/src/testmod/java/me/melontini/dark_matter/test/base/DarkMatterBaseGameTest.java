package me.melontini.dark_matter.test.base;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class DarkMatterBaseGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
  public void mixinAudit(TestContext context) {
    MixinEnvironment.getCurrentEnvironment().audit();
    context.complete();
  }
}
