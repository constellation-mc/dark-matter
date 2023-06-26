package me.melontini.dark_matter.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.enums.util.EnumUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.vehicle.BoatEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BoatEntity.Type.class, priority = 1001)
public class BoatEntityTypeMixin implements ExtendableEnum<BoatEntity.Type> {
    @Shadow
    @Final
    @Mutable
    private static BoatEntity.Type[] field_7724;

    @Invoker("<init>")
    static BoatEntity.Type dark_matter$invokeCtx(String internalName, int id, Block base, String name) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    private static BoatEntity.Type dark_matter$extendEnum(String internalName, Block base, String name) {
        BoatEntity.Type last = field_7724[field_7724.length - 1];
        BoatEntity.Type enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1, base, name);
        field_7724 = ArrayUtils.add(field_7724, enumConst);
        EnumUtils.clearEnumCache(BoatEntity.Type.class);
        return enumConst;
    }

    @Override
    public BoatEntity.Type dark_matter$extend(String internalName, Object... params) {
        return dark_matter$extendEnum(internalName, (Block) params[0], (String) params[1]);
    }
}
