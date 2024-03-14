package me.melontini.dark_matter.impl.mirage;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Lifecycle;
import me.melontini.dark_matter.api.base.util.Utilities;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.classes.Lazy;
import me.melontini.dark_matter.api.minecraft.client.events.AfterFirstReload;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPresets;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class FakeWorld {

    public static final ThreadLocal<Boolean> LOADING = ThreadLocal.withInitial(() -> false);

    public static ClientWorld INSTANCE;

    private static CombinedDynamicRegistries<ServerDynamicRegistryType> getRegistries() {
        CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = ServerDynamicRegistryType.createCombinedDynamicRegistries();
        CombinedDynamicRegistries<ServerDynamicRegistryType> cdr2 = bootstrapBuiltin(combinedDynamicRegistries);

        DynamicRegistryManager.Immutable immutable = cdr2.getPrecedingRegistryManagers(ServerDynamicRegistryType.DIMENSIONS);

        DimensionOptionsRegistryHolder preset = WorldPresets.createDemoOptions(immutable);
        DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = preset.toConfig(new SimpleRegistry<>(RegistryKeys.DIMENSION, Lifecycle.stable()));

        return cdr2.with(ServerDynamicRegistryType.DIMENSIONS, dimensionsConfig.toDynamicRegistryManager());
    }

    private static CombinedDynamicRegistries<ServerDynamicRegistryType> bootstrapBuiltin(CombinedDynamicRegistries<ServerDynamicRegistryType> cdr) {
        RegistryWrapper.WrapperLookup pain = BuiltinRegistries.createWrapperLookup();


        List<? extends RegistryKey<? extends Registry<?>>> keys = RegistryLoader.DYNAMIC_REGISTRIES.stream().map(RegistryLoader.Entry::key).toList();

        List<SimpleRegistry<Object>> regs = new ArrayList<>();
        for (RegistryKey<? extends Registry<?>> key : keys) {
            SimpleRegistry<Object> registry = new SimpleRegistry<>(Utilities.cast(key), Lifecycle.stable());

            pain.getOptionalWrapper(key).ifPresent(impl -> impl.streamEntries().forEach(ref -> registry.add(ref.registryKey(), ref.value(), Lifecycle.stable())));

            registry.freeze();
            regs.add(registry);
        }
        DynamicRegistryManager.Immutable immutable1 = new DynamicRegistryManager.ImmutableImpl(regs).toImmutable();
        return cdr.with(ServerDynamicRegistryType.WORLDGEN, immutable1);
    }

    public static void init() {
        AfterFirstReload.EVENT.register(() -> {
            DarkMatterLog.info("Creating a fake ClientWorld. Hold tight!");

            try {
                LOADING.set(true);
                var regs = FakeWorld.getRegistries();

                ClientPlayNetworkHandler networkHandler = new ClientPlayNetworkHandler(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), null, new GameProfile(UUID.randomUUID(), "fake_profile_ratio"), null);
                networkHandler.combinedDynamicRegistries = ClientDynamicRegistryType.createCombinedDynamicRegistries().with(ClientDynamicRegistryType.REMOTE, new DynamicRegistryManager.ImmutableImpl(SerializableRegistries.streamDynamicEntries(regs)).toImmutable());

                INSTANCE = new ClientWorld(networkHandler,
                        new ClientWorld.Properties(Difficulty.EASY, false, false),
                        World.OVERWORLD,
                        regs.getPrecedingRegistryManagers(ServerDynamicRegistryType.DIMENSIONS).get(RegistryKeys.DIMENSION_TYPE).entryOf(DimensionTypes.OVERWORLD),
                        0, 0, null,
                        MinecraftClient.getInstance().worldRenderer, true, 0);
            } finally {
                LOADING.remove();
            }
        });
    }
}