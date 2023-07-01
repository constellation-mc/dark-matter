package me.melontini.dark_matter.mirage;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.UUID;

/**
 * An attempt at creating an "authentic" fake ClientWorld, with a minimum amount of nulls.
 */
public class FakeWorld extends ClientWorld {
    public static final ClientWorld FAKE_WORLD = new FakeWorld();
    public static final AlwaysBrightLightmapTextureManager ALWAYS_BRIGHT_LTM = new AlwaysBrightLightmapTextureManager();
    private FakeWorld() {
        super(new ClientPlayNetworkHandler(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), new GameProfile(UUID.randomUUID(), "fake_profile_ratio"), null),
                new Properties(Difficulty.EASY, false, false),
                World.OVERWORLD,
                DynamicRegistryManager.BUILTIN.get().get(Registry.DIMENSION_TYPE_KEY).entryOf(DimensionType.OVERWORLD_REGISTRY_KEY),
                0, 0, null,
                MinecraftClient.getInstance().worldRenderer, true, 0);
    }

    public static void init() {

    }

    public static class AlwaysBrightLightmapTextureManager extends LightmapTextureManager {
        public AlwaysBrightLightmapTextureManager() {
            super(MinecraftClient.getInstance().gameRenderer, MinecraftClient.getInstance());
        }

        @Override
        public void update(float delta) {
            //no updates for you
        }
    }
}
