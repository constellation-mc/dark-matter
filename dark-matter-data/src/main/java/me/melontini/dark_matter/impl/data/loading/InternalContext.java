package me.melontini.dark_matter.impl.data.loading;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;

public record InternalContext(DynamicRegistryManager.Immutable manager, FeatureSet featureSet, DataPackContents contents) {
    public static final ThreadLocal<InternalContext> LOCAL = ThreadLocal.withInitial(() -> null);
}
