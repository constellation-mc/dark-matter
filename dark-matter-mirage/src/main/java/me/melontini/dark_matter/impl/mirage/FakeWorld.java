package me.melontini.dark_matter.impl.mirage;

import com.google.common.base.Suppliers;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Lifecycle;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.minecraft.client.events.AfterFirstReload;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPresets;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@UtilityClass
public class FakeWorld {

    public static final ThreadLocal<Boolean> LOADING = ThreadLocal.withInitial(() -> false);

    public static Supplier<ClientWorld> INSTANCE = Suppliers.memoize(() -> {
        DarkMatterLog.info("Creating a fake ClientWorld. Hold tight!");

        try {
            LOADING.set(true);
            var regs = FakeWorld.getRegistries();

            var immutable = ClientDynamicRegistryType.createCombinedDynamicRegistries().with(ClientDynamicRegistryType.REMOTE, new DynamicRegistryManager.ImmutableImpl(SerializableRegistries.streamDynamicEntries(regs)).toImmutable());

            ClientPlayNetworkHandler networkHandler = new ClientPlayNetworkHandler(MinecraftClient.getInstance(), new ClientConnection(NetworkSide.CLIENTBOUND), new ClientConnectionState(
                    new GameProfile(UUID.randomUUID(), "fake_profile_ratio"), null, immutable.getCombinedRegistryManager(), FeatureFlags.FEATURE_MANAGER.getFeatureSet(), null, null, null));

            return new ClientWorld(networkHandler,
                    new ClientWorld.Properties(Difficulty.EASY, false, false),
                    World.OVERWORLD,
                    regs.getPrecedingRegistryManagers(ServerDynamicRegistryType.DIMENSIONS).get(RegistryKeys.DIMENSION_TYPE).entryOf(DimensionTypes.OVERWORLD),
                    0, 0, null,
                    MinecraftClient.getInstance().worldRenderer, true, 0);
        } finally {
            LOADING.remove();
        }
    });

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
        AfterFirstReload.EVENT.register(() -> INSTANCE.get());
    }
}