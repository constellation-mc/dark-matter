package me.melontini.dark_matter.test.minecraft;

import java.time.Duration;
import me.melontini.dark_matter.api.base.util.MathUtil;
import me.melontini.dark_matter.api.minecraft.debug.ValueTracker;
import me.melontini.handytests.client.ClientTestContext;
import me.melontini.handytests.client.ClientTestEntrypoint;
import org.apache.commons.lang3.mutable.MutableInt;

public class ValueTrackerTest implements ClientTestEntrypoint {
  @Override
  public void onClientTest(ClientTestContext context) {
    MutableInt mutableInt = new MutableInt(10);
    context.submitAndWait(client -> {
      ValueTracker.addTracker("dm-test-random", mutableInt::getValue);
      return null;
    });
    context.takeScreenshot("value-tracker-0");
    mutableInt.add(MathUtil.nextInt(20, 1000));
    context.takeScreenshot("value-tracker-1");

    ValueTracker.removeTracker("dm-test-random");

    for (int i = 0; i < 25; i++) {
      ValueTracker.addTracker("dm-multiple-trackers-" + i, () -> MathUtil.nextDouble(0, 34));
    }
    context.takeScreenshot("value-tracker-multiple");
    for (int i = 0; i < 25; i++) {
      ValueTracker.removeTracker("dm-multiple-trackers-" + i);
    }

    context.submitAndWait(client -> {
      ValueTracker.addTracker(
          "dm-test-random", () -> "tracker with duration", Duration.ofSeconds(3));
      return null;
    });
    context.takeScreenshot("value-tracker-timer-start");
    context.waitForWorldTicks(20 * 4);
    context.takeScreenshot("value-tracker-timer-end");
  }
}
