package me.melontini.dark_matter.impl.mirage;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

@ApiStatus.Internal
public class FakeWorld extends ClientWorld {

    public static final ClientWorld INSTANCE = new FakeWorld();

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

}
