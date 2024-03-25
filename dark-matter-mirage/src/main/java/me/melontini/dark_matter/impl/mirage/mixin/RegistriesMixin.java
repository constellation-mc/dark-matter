package me.melontini.dark_matter.impl.mirage.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.melontini.dark_matter.impl.mirage.FakeWorld;
import net.minecraft.registry.RegistryBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RegistryBuilder.class)
public class RegistriesMixin {

    @WrapWithCondition(at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/RegistryBuilder$Registries;checkUnreferencedKeys()V"), method = "createWrapperLookup(Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;")
    private boolean dark_matter$checkUnreferencedKeys(RegistryBuilder.Registries instance) {
        return !FakeWorld.LOADING.get();
    }

    @WrapWithCondition(at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/RegistryBuilder$Registries;checkOrphanedValues()V"), method = "createWrapperLookup(Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;")
    private boolean dark_matter$checkOrphanedValues(RegistryBuilder.Registries instance) {
        return !FakeWorld.LOADING.get();
    }
}
