package me.melontini.dark_matter.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.enums.util.EnumUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.village.raid.Raid;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Raid.Member.class, priority = 1001)
public abstract class RaidMemberMixin implements ExtendableEnum<Raid.Member> {
    @Shadow
    @Final
    @Mutable
    private static Raid.Member[] field_16632;

    @Shadow
    @Final
    @Mutable
    private static Raid.Member[] VALUES;

    @Invoker("<init>")
    static Raid.Member dark_matter$invokeCtx(String internalName, int id, EntityType<? extends RaiderEntity> type, int[] countInWave) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    private static Raid.Member dark_matter$extendEnum(String internalName, EntityType<? extends RaiderEntity> type, int[] countInWave) {
        Raid.Member last = field_16632[field_16632.length - 1];
        Raid.Member enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1, type, countInWave);
        field_16632 = ArrayUtils.add(field_16632, enumConst);
        EnumUtils.clearEnumCache(Raid.Member.class);
        VALUES = Raid.Member.values();
        return enumConst;
    }

    @Override
    public Raid.Member dark_matter$extend(String internalName, Object... params) {
        return dark_matter$extendEnum(internalName, (EntityType<? extends RaiderEntity>) params[0], (int[]) params[1]);
    }
}
