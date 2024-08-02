package me.melontini.dark_matter.impl.data.mixin.loading;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.Set;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import me.melontini.dark_matter.impl.data.loading.ContextImpl;
import me.melontini.dark_matter.impl.data.loading.InternalContentsAccessor;
import me.melontini.dark_matter.impl.data.loading.InternalContext;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourceManagerHelperImpl.class)
public class ResourceManagerHelperImplMixin {

  @ModifyExpressionValue(
      at = @At(value = "CONSTANT", args = "intValue=-1"),
      method = "sort(Ljava/util/List;)V",
      remap = false)
  private int executeEvent(
      int lastSize,
      @Local(argsOnly = true) List<ResourceReloader> reloaders,
      @Local(ordinal = 1) List<IdentifiableResourceReloadListener> toAdd,
      @Local Set<Identifier> resolvedIds) {
    if (InternalContext.LOCAL.get() != null) {
      var internal = InternalContext.LOCAL.get();
      ContextImpl context = new ContextImpl(
          internal.manager(),
          internal.featureSet(),
          listener -> {
            if (!resolvedIds.add(listener.getFabricId())) {
              throw new IllegalStateException(
                  "Tried to register a reloader (%s) twice!".formatted(listener.getFabricId()));
            }
            toAdd.add(listener);
          },
          reloaderType ->
              ((InternalContentsAccessor) internal.contents()).dm$getReloader(reloaderType));

      ServerReloadersEvent.EVENT.invoker().onServerReloaders(context);
    }
    return lastSize;
  }

  @Inject(at = @At("TAIL"), method = "sort(Ljava/util/List;)V", remap = false)
  private void setEventResults(List<ResourceReloader> listeners, CallbackInfo ci) {
    if (InternalContext.LOCAL.get() != null) {
      var internal = InternalContext.LOCAL.get();
      var cls = IdentifiableResourceReloadListener.class;
      ((InternalContentsAccessor) internal.contents())
          .dark_matter$setReloaders(
              listeners.stream().filter(cls::isInstance).map(cls::cast).toList());
    }
  }
}
