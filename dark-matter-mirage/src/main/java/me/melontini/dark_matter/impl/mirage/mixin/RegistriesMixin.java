package me.melontini.dark_matter.impl.mirage.mixin;

import me.melontini.dark_matter.impl.mirage.FakeWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net/minecraft/registry/RegistryBuilder$Registries")
public class RegistriesMixin {

    @Shadow @Final private List<RuntimeException> errors;

    @Inject(at = @At("TAIL"), method = "validateReferences")
    private void dark_matter$validateRefs(CallbackInfo ci) {
        if (FakeWorld.LOADING.get()) {
            this.errors.clear();
        }
    }
}
