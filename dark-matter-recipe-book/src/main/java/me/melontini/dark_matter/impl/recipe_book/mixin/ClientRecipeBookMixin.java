package me.melontini.dark_matter.impl.recipe_book.mixin;

import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.impl.recipe_book.ClientRecipeBookUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Inject(at = @At("HEAD"), method = "getGroupForRecipe", cancellable = true)
    private static void dark_matter$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        var e = ClientRecipeBookUtils.forType(recipe.getType(), false);
        if (e == null) return;

        Optional<DynamicRegistryManager> registryManager = Optional.ofNullable(MinecraftClient.getInstance().getNetworkHandler()).map(ClientPlayNetworkHandler::getRegistryManager);
        RecipeBookGroup group = e.invoker().onRecipeBookGroupLookup(recipe.getId(), Utilities.cast(recipe), registryManager.orElse(null));
        if (group != null) cir.setReturnValue(group);
    }
}
