package me.melontini.dark_matter.api.mirage;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.mirage.AlwaysBrightLightmapTextureManager;
import me.melontini.dark_matter.impl.mirage.FakeWorld;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;

@UtilityClass
public class Mirage {

    public static ClientWorld getFakeWorld() {
        return FakeWorld.INSTANCE.get();
    }

    public static LightmapTextureManager getAlwaysBrightLTM() {
        return AlwaysBrightLightmapTextureManager.INSTANCE;
    }
}
