package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.api.enums.EnumUtils;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = RecipeBookGroup.class, priority = 1001)
public class RecipeBookGroupMixin implements ExtendableEnum<RecipeBookGroup> {

    @Final
    @Shadow
    @Mutable
    private static RecipeBookGroup[] field_1805;

    @Invoker("<init>")
    static RecipeBookGroup dark_matter$invokeCtx(String internalName, int id, ItemStack... stacks) {
        throw new IllegalStateException("<init> invoker not implemented");
    }

    @Unique
    private static RecipeBookGroup dark_matter$extendEnum(String internalName, ItemStack... stacks) {
        for (RecipeBookGroup group : field_1805) {
            if (group.name().equalsIgnoreCase(internalName)) return group;
        }

        RecipeBookGroup last = field_1805[field_1805.length - 1];
        RecipeBookGroup enumConst = dark_matter$invokeCtx(internalName, last.ordinal() + 1, stacks);
        field_1805 = ArrayUtils.add(field_1805, enumConst);
        EnumUtils.clearEnumCache(RecipeBookGroup.class);
        return enumConst;
    }

    @Override
    public RecipeBookGroup dark_matter$extend(String internalName, Object... params) {
        return dark_matter$extendEnum(internalName, (ItemStack[]) params[0]);
    }
}
