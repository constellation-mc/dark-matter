package me.melontini.dark_matter.impl.data.loading;

import me.melontini.dark_matter.api.data.loading.ReloaderType;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;

import java.util.function.Consumer;
import java.util.function.Function;

public record ContextImpl(DynamicRegistryManager.Immutable registryManager,
                          FeatureSet enabledFeatures,
                          Consumer<IdentifiableResourceReloadListener> registrar,
                          Function<ReloaderType<?>, ResourceReloader> provider) implements ServerReloadersEvent.Context {

    public void register(IdentifiableResourceReloadListener listener) {
        registrar().accept(listener);
    }

    @Override
    public <T extends ResourceReloader> T reloader(ReloaderType<T> type) {
        return (T) provider().apply(type);
    }
}
