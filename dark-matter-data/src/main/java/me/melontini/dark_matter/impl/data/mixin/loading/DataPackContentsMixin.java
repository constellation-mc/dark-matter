package me.melontini.dark_matter.impl.data.mixin.loading;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.dark_matter.api.data.loading.ReloaderType;
import me.melontini.dark_matter.impl.data.loading.InternalContentsAccessor;
import me.melontini.dark_matter.impl.data.loading.InternalContext;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = DataPackContents.class, priority = 1100)
abstract class DataPackContentsMixin implements InternalContentsAccessor {

  @Unique private final Map<Identifier, IdentifiableResourceReloadListener> reloadersByIdentifier =
      new HashMap<>();

  @Unique private final IdentityHashMap<ReloaderType<?>, IdentifiableResourceReloadListener>
      reloadersByType = new IdentityHashMap<>();

  @Override
  public <T extends ResourceReloader> T dm$getReloader(ReloaderType<T> type) {
    var reloader = this.reloadersByType.get(type);
    if (reloader == null) {
      synchronized (this.reloadersByIdentifier) {
        reloader = this.reloadersByIdentifier.get(type.identifier());
        if (reloader == null)
          throw new NoSuchElementException("Missing reloader %s".formatted(type.identifier()));
        this.reloadersByType.put(type, reloader);
      }
    }
    return (T) reloader;
  }

  @Override
  public void dark_matter$setReloaders(List<IdentifiableResourceReloadListener> reloaders) {
    this.reloadersByIdentifier.clear();
    this.reloadersByType.clear();

    for (IdentifiableResourceReloadListener reloader : reloaders) {
      this.reloadersByIdentifier.put(reloader.getFabricId(), reloader);
    }
  }

  @WrapOperation(
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/resource/SimpleResourceReload;start(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/resource/ResourceReload;"),
      method = "method_58296")
  private static ResourceReload setContext(
      ResourceManager manager,
      List<ResourceReloader> reloaders,
      Executor prepareExecutor,
      Executor applyExecutor,
      CompletableFuture<Unit> initialStage,
      boolean profiled,
      Operation<ResourceReload> original,
      @Local DataPackContents contents,
      @Local(argsOnly = true)
          CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries,
      @Local(argsOnly = true) FeatureSet featureSet) {
    try {
      InternalContext.LOCAL.set(new InternalContext(
          dynamicRegistries.getCombinedRegistryManager(), featureSet, contents));
      return original.call(
          manager, reloaders, prepareExecutor, applyExecutor, initialStage, profiled);
    } finally {
      InternalContext.LOCAL.remove();
    }
  }
}
