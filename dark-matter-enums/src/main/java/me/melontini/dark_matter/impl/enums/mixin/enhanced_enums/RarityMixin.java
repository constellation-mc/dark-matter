package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.api.base.util.mixin.Publicize;
import me.melontini.dark_matter.api.enums.EnumUtils;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
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

    @Unique
    @Publicize
    private static Rarity dark_matter$extendEnum(String internalName, Formatting formatting) {
        for (Rarity rarity : field_8905) {
            if (rarity.name().equalsIgnoreCase(internalName)) return rarity;
        }

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
