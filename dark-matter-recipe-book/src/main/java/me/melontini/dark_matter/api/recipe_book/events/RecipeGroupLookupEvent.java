package me.melontini.dark_matter.api.recipe_book.events;

import me.melontini.dark_matter.impl.recipe_book.ClientRecipeBookUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Allows you to map a recipe to a group.
 * <p>
 * It's recommended to keep this simple, as it will be run for every recipe.
 */
@Environment(EnvType.CLIENT)
public interface RecipeGroupLookupEvent<T extends Recipe<?>> {

    RecipeBookGroup lookup(Identifier id, T recipe, @Nullable DynamicRegistryManager registryManager);

    static <T extends Recipe<?>> Event<RecipeGroupLookupEvent<T>> forType(RecipeType<T> type) {
        return ClientRecipeBookUtils.forType(type, true);
    }
}
