package me.melontini.dark_matter.impl.data.loading;

import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;

import java.util.function.Consumer;

public record ContextImpl(DynamicRegistryManager.Immutable registryManager, FeatureSet enabledFeatures,
                          Consumer<IdentifiableResourceReloadListener> registrar) implements ServerReloadersEvent.Context {

    public void register(IdentifiableResourceReloadListener listener) {
        registrar().accept(listener);
    }
}
