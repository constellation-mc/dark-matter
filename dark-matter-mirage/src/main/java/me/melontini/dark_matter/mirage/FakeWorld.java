package me.melontini.dark_matter.mirage;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import me.melontini.dark_matter.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoading;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An attempt at creating an "authentic" fake ClientWorld, with a minimum amount of nulls.
 */
public class FakeWorld extends ClientWorld {

    public static ClientWorld FAKE_WORLD;
    public static final AlwaysBrightLightmapTextureManager ALWAYS_BRIGHT_LTM = new AlwaysBrightLightmapTextureManager();

    static {
        DarkMatterLog.info("Creating a fake ClientWorld. Hold tight!");

        var isl = MinecraftClient.getInstance().createIntegratedServerLoader();

        LevelStorage.Session session = isl.createSession("dark_matter_fake_world_please_ignore");
        ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(session);
        DataConfiguration dataConfiguration = MinecraftServer.DEMO_LEVEL_INFO.getDataConfiguration();
        SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, dataConfiguration, true, false);

        Function<DynamicRegistryManager, DimensionOptionsRegistryHolder> function = WorldPresets::createDemoOptions;
        try {
            Pair<DataConfiguration, LifecycledResourceManager> pair = dataPacks.load();
            LifecycledResourceManager lifecycledResourceManager = pair.getSecond();
            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = ServerDynamicRegistryType.createCombinedDynamicRegistries();
            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries2 = SaveLoading.withRegistriesLoaded(
                    lifecycledResourceManager, combinedDynamicRegistries, ServerDynamicRegistryType.WORLDGEN, RegistryLoader.DYNAMIC_REGISTRIES
            );
            DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries2.getPrecedingRegistryManagers(ServerDynamicRegistryType.DIMENSIONS);
            DynamicRegistryManager.Immutable immutable2 = RegistryLoader.load(lifecycledResourceManager, immutable, RegistryLoader.DIMENSION_REGISTRIES);

            SaveLoading.LoadContextSupplierContext context = new SaveLoading.LoadContextSupplierContext(lifecycledResourceManager, dataConfiguration, immutable, immutable2);
            DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = function.apply(
                            context.worldGenRegistryManager())
                    .toConfig(context.dimensionsRegistryManager().get(RegistryKeys.DIMENSION));
            SaveLoading.LoadContext<?> loadContext = new SaveLoading.LoadContext<>(
                    new LevelProperties(MinecraftServer.DEMO_LEVEL_INFO, GeneratorOptions.DEMO_OPTIONS, dimensionsConfig.specialWorldProperty(), dimensionsConfig.getLifecycle()),
                    dimensionsConfig.toDynamicRegistryManager());
            CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries3 = combinedDynamicRegistries2.with(
                    ServerDynamicRegistryType.DIMENSIONS,
                    loadContext.dimensionsRegistryManager());

            ClientPlayNetworkHandler networkHandler = new ClientPlayNetworkHandler(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), new ServerInfo("fake_name", "0.0.0.0", true), new GameProfile(UUID.randomUUID(), "fake_profile_ratio"), null);
            networkHandler.combinedDynamicRegistries = ClientDynamicRegistryType.createCombinedDynamicRegistries().with(ClientDynamicRegistryType.REMOTE, new DynamicRegistryManager.ImmutableImpl(SerializableRegistries.streamDynamicEntries(combinedDynamicRegistries3)).toImmutable());

            FAKE_WORLD = new FakeWorld(networkHandler,
                    new Properties(Difficulty.EASY, false, false), World.OVERWORLD, combinedDynamicRegistries3.getPrecedingRegistryManagers(ServerDynamicRegistryType.DIMENSIONS).get(RegistryKeys.DIMENSION_TYPE).entryOf(DimensionTypes.OVERWORLD), 0, 0, null, MinecraftClient.getInstance().worldRenderer, true, 0);

            Path path = FabricLoader.getInstance().getGameDir().resolve("saves/dark_matter_fake_world_please_ignore");
            if (Files.exists(path)) {
                FileUtils.deleteDirectory(path.toFile());
            }
        } catch (IOException e) {
            DarkMatterLog.warn("Couldn't delete FakeWorld's directory.", e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private FakeWorld(ClientPlayNetworkHandler networkHandler, Properties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimensionTypeEntry, int loadDistance, int simulationDistance, Supplier<Profiler> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed) {
        super(networkHandler, properties, registryRef, dimensionTypeEntry, loadDistance, simulationDistance, profiler, worldRenderer, debugWorld, seed);
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