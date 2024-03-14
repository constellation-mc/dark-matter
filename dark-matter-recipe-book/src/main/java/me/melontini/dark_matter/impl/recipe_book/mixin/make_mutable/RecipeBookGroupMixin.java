package me.melontini.dark_matter.impl.recipe_book.mixin.make_mutable;

import me.melontini.dark_matter.api.mixin.annotations.AsmTransformers;
import me.melontini.dark_matter.impl.recipe_book.transformers.RecipeBookMutator;
import net.minecraft.client.recipebook.RecipeBookGroup;
import org.spongepowered.asm.mixin.Mixin;

@AsmTransformers(RecipeBookMutator.class)
@Mixin(value = RecipeBookGroup.class, priority = 800)
public class RecipeBookGroupMixin {

}
