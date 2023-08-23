package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.api.base.util.mixin.Publicize;
import me.melontini.dark_matter.api.enums.EnumUtils;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
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

    @Unique
    @Publicize
    private static RecipeBookCategory dark_matter$extendEnum(String internalName) {
        for (RecipeBookCategory category : field_25767) {
            if (category.name().equalsIgnoreCase(internalName)) return category;
        }

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
