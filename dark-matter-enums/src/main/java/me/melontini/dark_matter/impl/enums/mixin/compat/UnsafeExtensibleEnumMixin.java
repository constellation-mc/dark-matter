package me.melontini.dark_matter.impl.enums.mixin.compat;

import me.melontini.dark_matter.api.mixin.annotations.MixinPredicate;
import me.melontini.dark_matter.api.mixin.annotations.Mod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("UnresolvedMixinReference")
@Pseudo
@Mixin(targets = "fuzs/extensibleenums/core/UnsafeExtensibleEnum")
@MixinPredicate(mods = @Mod(value = "extensibleenums", version = "<=7.0.0"))
public class UnsafeExtensibleEnumMixin {
    @ModifyConstant(method = "addToEnumValues", constant = @Constant(intValue = 4122), remap = false, require = 0)
    private static int dark_matter$skipFinalCheck(int value) {
        return 4106;
    }
}
