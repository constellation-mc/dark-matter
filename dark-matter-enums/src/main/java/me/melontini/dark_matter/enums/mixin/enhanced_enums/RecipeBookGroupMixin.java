package me.melontini.dark_matter.enums.mixin.enhanced_enums;

import me.melontini.dark_matter.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.enums.util.EnumUtils;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
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

    private static RecipeBookGroup dark_matter$extendEnum(String internalName, ItemStack... stacks) {
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
