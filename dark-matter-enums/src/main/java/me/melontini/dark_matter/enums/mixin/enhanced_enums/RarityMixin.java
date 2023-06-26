package me.melontini.dark_matter.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.enums.util.EnumUtils;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Rarity.class, priority = 1001)
public class RarityMixin implements ExtendableEnum<Rarity> {
    @Final
    @Shadow
    @Mutable
    private static Rarity[] field_8905;

    @Invoker("<init>")
    static Rarity dark_matter$invokeCtx(String internalName, int id, Formatting formatting) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    private static Rarity dark_matter$extendEnum(String internalName, Formatting formatting) {
        Rarity last = field_8905[field_8905.length - 1];
        Rarity enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1, formatting);
        field_8905 = ArrayUtils.add(field_8905, enumConst);
        EnumUtils.clearEnumCache(Rarity.class);
        return enumConst;
    }

    @Override
    public Rarity dark_matter$extend(String internalName, Object... params) {
        return dark_matter$extendEnum(internalName, (Formatting) params[0]);
    }
}
