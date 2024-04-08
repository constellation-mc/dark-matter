package me.melontini.dark_matter.test.mixin;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.mixin.VirtualMixins;
import org.spongepowered.asm.mixin.Mixins;

import java.io.ByteArrayInputStream;

public class VirtualMixinsTest {

    private static final String CONFIG = "dm-mixins-test-config";
    private static final String CONTENTS = """
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
        VirtualMixins.addMixins(acceptor -> acceptor.add(CONFIG, new ByteArrayInputStream(CONTENTS.getBytes())));
    }

    public static void onInitialize() {
        MakeSure.isTrue(Mixins.getConfigs().stream().filter(config -> CONFIG.equals(config.getName())).findFirst().isEmpty(), "Unvisited mixin config!");
    }
}
