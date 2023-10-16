package me.melontini.dark_matter.impl.mirage;

import com.mojang.authlib.GameProfile;
import me.melontini.dark_matter.api.base.reflect.UnsafeAccess;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
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
import java.util.concurrent.Callable;

@ApiStatus.Internal
public class FakeWorld {

    public static final Lazy<ClientWorld> INSTANCE = Lazy.of(() -> () -> {
        try {
            DarkMatterLog.info("Creating a fake ClientWorld. Hold tight!");

            return new ClientWorld(
                    orNull(() -> new ClientPlayNetworkHandler(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), new GameProfile(UUID.randomUUID(), "fake_profile_ratio"), null)),
                    orNull(() -> new ClientWorld.Properties(Difficulty.EASY, false, false)),
                    World.OVERWORLD,
                    orNull(() -> DynamicRegistryManager.BUILTIN.get().get(Registry.DIMENSION_TYPE_KEY).entryOf(DimensionType.OVERWORLD_REGISTRY_KEY)),
                    0, 0, null,
                    MinecraftClient.getInstance().worldRenderer, false, 0
            );
        } catch (Throwable e) {
            DarkMatterLog.error("Failed to create fake world, falling back to unsafe", e);
            return UnsafeAccess.allocateInstance(ClientWorld.class);
        }
    });

    private static <T> T orNull(Callable<T> c) {
        try {
            return c.call();
        } catch (Throwable e) {
            return null;
        }
    }

    public static void init() {
        INSTANCE.get();
    }
}
