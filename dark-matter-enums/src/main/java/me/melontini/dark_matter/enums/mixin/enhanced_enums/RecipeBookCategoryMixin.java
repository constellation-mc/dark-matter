package me.melontini.dark_matter.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.enums.util.EnumUtils;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = RecipeBookCategory.class, priority = 1001)
public class RecipeBookCategoryMixin implements ExtendableEnum<RecipeBookCategory> {
    @Shadow
    @Final
    @Mutable
    private static RecipeBookCategory[] field_25767;

    @Invoker("<init>")
    static RecipeBookCategory dark_matter$invokeCtx(String internalName, int id) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    private static RecipeBookCategory dark_matter$extendEnum(String internalName) {
        RecipeBookCategory last = field_25767[field_25767.length - 1];
        RecipeBookCategory enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1);
        field_25767 = ArrayUtils.add(field_25767, enumConst);
        EnumUtils.clearEnumCache(RecipeBookCategory.class);
        return enumConst;
    }

    @Override
    public RecipeBookCategory dark_matter$extend(String internalName, Object... params) {
        return dark_matter$extendEnum(internalName);
    }
}
