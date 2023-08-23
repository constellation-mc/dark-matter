package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.api.base.util.mixin.Publicize;
import me.melontini.dark_matter.api.enums.EnumUtils;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
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

    @Unique
    @Publicize
    private static AbstractMinecartEntity.Type dark_matter$extendEnum(String internalName) {
        for (AbstractMinecartEntity.Type type : field_7673) {
            if (type.name().equalsIgnoreCase(internalName)) return type;
        }

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
