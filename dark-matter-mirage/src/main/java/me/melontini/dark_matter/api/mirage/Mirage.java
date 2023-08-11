package me.melontini.dark_matter.api.mirage;

import me.melontini.dark_matter.impl.mirage.AlwaysBrightLightmapTextureManager;
import me.melontini.dark_matter.impl.mirage.FakeWorld;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;

public class Mirage {

    public static final ClientWorld FAKE_WORLD = FakeWorld.INSTANCE;
    public static final LightmapTextureManager ALWAYS_BRIGHT_LTM = AlwaysBrightLightmapTextureManager.INSTANCE;

}
