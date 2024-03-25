package me.melontini.dark_matter.impl.data.mixin.loading;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.melontini.dark_matter.api.data.loading.ReloaderType;
import me.melontini.dark_matter.impl.data.loading.ContextImpl;
import me.melontini.dark_matter.api.data.loading.DataPackContentsAccessor;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.Function;

@Mixin(value = DataPackContents.class, priority = 1100)
abstract class DataPackContentsMixin implements DataPackContentsAccessor {

    @Shadow public abstract List<ResourceReloader> getContents();

    @Unique
    private Map<Identifier, IdentifiableResourceReloadListener> reloadersMap;
    @Unique
    private List<IdentifiableResourceReloadListener> reloaders;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void dark_matter$addReloaders(DynamicRegistryManager.Immutable dynamicRegistryManager, FeatureSet enabledFeatures, CommandManager.RegistrationEnvironment environment, int functionPermissionLevel, CallbackInfo ci) {
        List<IdentifiableResourceReloadListener> list = new ArrayList<>();
        ServerReloadersEvent.EVENT.invoker().register(new ContextImpl(dynamicRegistryManager, enabledFeatures, list::add, this::dm$getReloader));
        this.reloaders = ImmutableList.copyOf(list);

        var cls = IdentifiableResourceReloadListener.class;
        this.reloadersMap = getContents().stream().filter(cls::isInstance).map(cls::cast)
                .collect(ImmutableMap.toImmutableMap(IdentifiableResourceReloadListener::getFabricId, Function.identity()));
    }

    @Override
    public <T extends ResourceReloader> T dm$getReloader(ReloaderType<T> type) {
        return (T) Objects.requireNonNull(this.reloadersMap.get(type.identifier()), () -> "Missing reloader %s".formatted(type.identifier()));
    }

    @ModifyReturnValue(at = @At("RETURN"), method = "getContents")
    private List<ResourceReloader> dark_matter$injectContents(List<ResourceReloader> original) {
        if (this.reloaders.isEmpty()) return original;
        return Streams.concat(original.stream(), this.reloaders.stream()).distinct().toList();
    }
}
