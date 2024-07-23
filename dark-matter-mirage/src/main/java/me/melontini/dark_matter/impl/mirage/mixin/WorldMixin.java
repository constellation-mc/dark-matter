package me.melontini.dark_matter.impl.mirage.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.melontini.dark_matter.impl.mirage.FakeWorld;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(World.class)
public class WorldMixin {

  @WrapOperation(
      at =
          @At(
              value = "NEW",
              target =
                  "(Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/entity/damage/DamageSources;"),
      method = "<init>")
  private DamageSources dark_matter$ignoreDamageSources(
      DynamicRegistryManager registryManager, Operation<DamageSources> original) {
    if (FakeWorld.LOADING.get()) {
      return null;
    }
    return original.call(registryManager);
  }
}
