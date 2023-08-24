package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.api.base.util.mixin.annotations.Publicize;
import me.melontini.dark_matter.api.enums.EnumUtils;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.village.raid.Raid;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
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

    @Unique
    @Publicize
    private static Raid.Member dark_matter$extendEnum(String internalName, EntityType<? extends RaiderEntity> type, int[] countInWave) {
        for (Raid.Member member : field_16632) {
            if (member.name().equalsIgnoreCase(internalName)) return member;
        }

        Raid.Member last = field_16632[field_16632.length - 1];
        Raid.Member enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1, type, countInWave);
        field_16632 = ArrayUtils.add(field_16632, enumConst);
        EnumUtils.clearEnumCache(Raid.Member.class);
        VALUES = Raid.Member.values();
        DarkMatterLog.debug("Extended enum {}", enumConst);
        return enumConst;
    }

    @Override
    public Raid.Member dark_matter$extend(String internalName, Object... params) {
        return dark_matter$extendEnum(internalName, (EntityType<? extends RaiderEntity>) params[0], (int[]) params[1]);
    }
}
