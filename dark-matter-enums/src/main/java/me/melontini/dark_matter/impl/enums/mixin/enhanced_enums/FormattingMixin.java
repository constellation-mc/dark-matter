package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.api.base.util.mixin.annotations.Publicize;
import me.melontini.dark_matter.api.enums.EnumUtils;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Formatting.class, priority = 1001)
public class FormattingMixin implements ExtendableEnum<Formatting> {

    @Shadow
    @Final
    @Mutable
    private static Formatting[] field_1072;

    @Invoker("<init>")
    static Formatting dark_matter$invokeCtx(String internalName, int id, String name, char code, boolean modifier, int colorIndex, @Nullable Integer colorValue) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    @Invoker("<init>")
    static Formatting dark_matter$invokeCtx(String internalName, int id, String name, char code, int colorIndex, @Nullable Integer colorValue) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    @Invoker("<init>")
    static Formatting dark_matter$invokeCtx(String internalName, int id, String name, char code, boolean modifier) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    @Unique
    @Publicize
    private static Formatting dark_matter$extendEnum(String internalName, String name, Character code, Boolean modifier, Integer colorIndex, @Nullable Integer colorValue) {
        for (Formatting formatting : field_1072) {
            if (formatting.name().equalsIgnoreCase(internalName)) return formatting;
        }

        Formatting last = field_1072[field_1072.length - 1];
        Formatting enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1, name, code, modifier, colorIndex, colorValue);
        field_1072 = ArrayUtils.add(field_1072, enumConst);
        EnumUtils.clearEnumCache(Formatting.class);
        return enumConst;
    }

    @Unique
    @Publicize
    private static Formatting dark_matter$extendEnum(String internalName, String name, Character code, Integer colorIndex, @Nullable Integer colorValue) {
        for (Formatting formatting : field_1072) {
            if (formatting.name().equalsIgnoreCase(internalName)) return formatting;
        }

        Formatting last = field_1072[field_1072.length - 1];
        Formatting enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1, name, code, colorIndex, colorValue);
        field_1072 = ArrayUtils.add(field_1072, enumConst);
        EnumUtils.clearEnumCache(Formatting.class);
        return enumConst;
    }

    @Unique
    @Publicize
    private static Formatting dark_matter$extendEnum(String internalName, String name, Character code, Boolean modifier) {
        for (Formatting formatting : field_1072) {
            if (formatting.name().equalsIgnoreCase(internalName)) return formatting;
        }

        Formatting last = field_1072[field_1072.length - 1];
        Formatting enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1, name, code, modifier);
        field_1072 = ArrayUtils.add(field_1072, enumConst);
        EnumUtils.clearEnumCache(Formatting.class);
        return enumConst;
    }
}
