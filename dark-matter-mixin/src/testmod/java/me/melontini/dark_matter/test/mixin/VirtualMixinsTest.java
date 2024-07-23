package me.melontini.dark_matter.test.mixin;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import me.melontini.dark_matter.api.mixin.VirtualMixins;
import org.assertj.core.api.Assertions;
import org.spongepowered.asm.mixin.Mixins;

public class VirtualMixinsTest {

  private static final String CONFIG = "dm-mixins-test-config";
  private static final String CONTENTS =
      """
            {
              "required": false,
              "minVersion": "0.8",
              "package": "me.melontini.dark_matter.test.mixin.mixin",
              "plugin": "me.melontini.dark_matter.test.mixin.ExtendablePluginTest",
              "compatibilityLevel": "JAVA_17",
              "mixins": [
              ],
              "client": [
              ],
              "injectors": {
                "defaultRequire": 0
              }
            }""";

  public static void onPreLaunch() {
    VirtualMixins.addMixins(acceptor ->
        acceptor.add(CONFIG, new ByteArrayInputStream(CONTENTS.getBytes(StandardCharsets.UTF_8))));
  }

  public static void onInitialize() {
    Assertions.assertThat(Mixins.getConfigs().stream())
        .noneMatch(config -> CONFIG.equals(config.getName()));
  }
}
