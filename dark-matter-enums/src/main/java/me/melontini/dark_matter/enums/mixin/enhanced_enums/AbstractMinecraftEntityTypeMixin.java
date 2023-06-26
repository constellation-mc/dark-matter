package me.melontini.dark_matter.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.enums.util.EnumUtils;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AbstractMinecartEntity.Type.class, priority = 1001)
public class AbstractMinecraftEntityTypeMixin implements ExtendableEnum<AbstractMinecartEntity.Type> {
    @Shadow
    @Final
    @Mutable
    private static AbstractMinecartEntity.Type[] field_7673;

    @Invoker("<init>")
    static AbstractMinecartEntity.Type dark_matter$invokeCtx(String internalName, int id) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    private static AbstractMinecartEntity.Type dark_matter$extendEnum(String internalName) {
        AbstractMinecartEntity.Type last = field_7673[field_7673.length - 1];
        AbstractMinecartEntity.Type enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1);
        field_7673 = ArrayUtils.add(field_7673, enumConst);
        EnumUtils.clearEnumCache(AbstractMinecartEntity.Type.class);
        return enumConst;
    }

    @Override
    public AbstractMinecartEntity.Type dark_matter$extend(String internalName, Object... params) {
        return dark_matter$extendEnum(internalName);
    }
}
