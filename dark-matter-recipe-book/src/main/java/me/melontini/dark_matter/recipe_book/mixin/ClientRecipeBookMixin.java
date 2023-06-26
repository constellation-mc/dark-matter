package me.melontini.dark_matter.recipe_book.mixin;

import me.melontini.dark_matter.recipe_book.RecipeBookHelper;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {
    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void dark_matter$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        if (RecipeBookHelper.hasHandlers(recipe.getType())) {
            RecipeBookGroup group;

            for (Function<Recipe<?>, RecipeBookGroup> function : RecipeBookHelper.getHandlers(recipe.getType())) {
                if ((group = function.apply(recipe)) != null) {
                    cir.setReturnValue(group);
                    return;
                }
            }
        }
    }
}
