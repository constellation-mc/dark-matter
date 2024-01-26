package me.melontini.dark_matter.impl.recipe_book.mixin;

import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.impl.recipe_book.ClientRecipeBookUtils;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void dark_matter$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        var e = ClientRecipeBookUtils.forType(recipe.getType(), false);
        if (e == null) return;

        RecipeBookGroup group = e.invoker().lookup(recipe.getId(), Utilities.cast(recipe));
        if (group != null) cir.setReturnValue(group);
    }
}
