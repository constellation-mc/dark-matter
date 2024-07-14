package me.melontini.dark_matter.api.data.loading;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;

/**
 * Allows registering new data pack reloaders with proper context. This supports proper managers akin to the RecipeManager and LootManager.
 * <p>
 * The Fabric API is side-agnostic, so you have to rely on static hooks. It also lacks a way to retrieve {@link DynamicRegistryManager}.
 * </p>
 * FAPI version 0.100.7+1.21 introduces a new reloader API which has {@link DynamicRegistryManager} context.
 */
public interface ServerReloadersEvent {

    Event<ServerReloadersEvent> EVENT = EventFactory.createArrayBacked(ServerReloadersEvent.class, events -> (c) -> {
        for (ServerReloadersEvent event : events) {
            event.onServerReloaders(c);
        }
    });

    void onServerReloaders(Context context);

    interface Context {
        DynamicRegistryManager registryManager();
        FeatureSet enabledFeatures();

        void register(IdentifiableResourceReloadListener reloadListener);
        <T extends ResourceReloader> T reloader(ReloaderType<T> type);
    }
}
