package me.melontini.dark_matter.impl.data.mixin.loading;

import me.melontini.dark_matter.api.data.loading.DataPackContentsAccessor;
import me.melontini.dark_matter.api.data.loading.ReloaderType;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin implements DataPackContentsAccessor {

  @Shadow
  private MinecraftServer.ResourceManagerHolder resourceManagerHolder;

  @Override
  public <T extends ResourceReloader> T dm$getReloader(ReloaderType<T> type) {
    return ((DataPackContentsAccessor) resourceManagerHolder.dataPackContents())
        .dm$getReloader(type);
  }
}
