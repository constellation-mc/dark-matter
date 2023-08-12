package me.melontini.dark_matter.api.recipe_book;

import me.melontini.dark_matter.impl.recipe_book.RecipeBookInternals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

public class RecipeBookHelper {

    private RecipeBookHelper() {
        throw new UnsupportedOperationException();
    }

    @Environment(EnvType.CLIENT)
    public static void addRecipePredicate(RecipeType<?> type, Function<Recipe<?>, RecipeBookGroup> function) {
        RecipeBookInternals.addRecipePredicate(type, function);
    }

    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, RecipeBookGroup group) {
        RecipeBookInternals.addToGetGroups(category, group);
    }

    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, int index, RecipeBookGroup group) {
        RecipeBookInternals.addToGetGroups(category, index, group);
    }

    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, List<RecipeBookGroup> groups) {
        RecipeBookInternals.addToGetGroups(category, groups);
    }

    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, int index, List<RecipeBookGroup> groups) {
        RecipeBookInternals.addToGetGroups(category, index, groups);
    }

    @Environment(EnvType.CLIENT)
    public static void addToSearchMap(RecipeBookGroup searchGroup, List<RecipeBookGroup> groups) {
        RecipeBookInternals.addToSearchMap(searchGroup, groups);
    }

    @Environment(EnvType.CLIENT)
    public static void addToSearchMap(RecipeBookGroup searchGroup, int index, List<RecipeBookGroup> groups) {
        RecipeBookInternals.addToSearchMap(searchGroup, index, groups);
    }

    public static RecipeBookCategory createCategory(Identifier id) {
        return RecipeBookInternals.createCategory(id.toString().replace('/', '_').replace(':', '_'));
    }

    @Environment(EnvType.CLIENT)
    public static RecipeBookGroup createGroup(Identifier id, ItemStack... stacks) {
        return RecipeBookInternals.createGroup(id.toString().replace('/', '_').replace(':', '_'), stacks);
    }
}
