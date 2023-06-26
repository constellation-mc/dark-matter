package me.melontini.dark_matter.enums.mixin.compat;

import me.melontini.dark_matter.util.mixin.MixinShouldApply;
import me.melontini.dark_matter.util.mixin.Mod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("UnresolvedMixinReference")
@Pseudo
@Mixin(targets = "fuzs/extensibleenums/core/UnsafeExtensibleEnum")
@MixinShouldApply(mods = @Mod("extensibleenums"))
public class UnsafeExtensibleEnumMixin {
    @ModifyConstant(method = "addToEnumValues", constant = @Constant(intValue = 4122), remap = false, require = 0)
    private static int dark_matter$skipFinalCheck(int value) {
        return 4106;
    }
}
