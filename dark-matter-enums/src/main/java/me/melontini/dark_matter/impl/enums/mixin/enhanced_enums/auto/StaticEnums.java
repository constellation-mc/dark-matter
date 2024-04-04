package me.melontini.dark_matter.impl.enums.mixin.enhanced_enums.auto;

import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.api.mixin.annotations.AsmTransformers;
import me.melontini.dark_matter.impl.enums.transformers.StaticEnumTransformer;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import java.util.function.Supplier;

@Pseudo
@AsmTransformers(StaticEnumTransformer.class)
@Mixin(value = {
        RecipeBookCategory.class,
        Rarity.class,
        Formatting.class,
        BoatEntity.Type.class,
        AbstractMinecartEntity.Type.class
}, priority = 1100)
public class StaticEnums<C extends Supplier<Object[]>> implements ExtendableEnum<C> {
}
