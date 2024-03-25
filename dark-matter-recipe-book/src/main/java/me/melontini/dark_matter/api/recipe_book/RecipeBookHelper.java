package me.melontini.dark_matter.api.recipe_book;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.recipe_book.ClientRecipeBookUtils;
import me.melontini.dark_matter.impl.recipe_book.RecipeBookUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public final class RecipeBookHelper {

    //
    // Register groups.
    //

    /**
     * Register groups for a category.
     * <p>
     * You'll also have to call {@link RecipeBookHelper#addToSearchGroup(RecipeBookGroup, RecipeBookGroup...)} to add the groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroups(@NotNull RecipeBookCategory category, RecipeBookGroup... groups) {
        registerGroups(category, Arrays.asList(groups));
    }

    /**
     * Register groups for a category. Here you can specify the index for the groups.
     * <p>
     * You'll also have to call {@link RecipeBookHelper#addToSearchGroup(RecipeBookGroup, RecipeBookGroup...)} to add the groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroups(@NotNull RecipeBookCategory category, int index, RecipeBookGroup... groups) {
        registerGroups(category, index, Arrays.asList(groups));
    }

    /**
     * Register groups for a category.
     * <p>
     * You'll also have to call {@link RecipeBookHelper#addToSearchGroup(RecipeBookGroup, RecipeBookGroup...)} to add the groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroups(@NotNull RecipeBookCategory category, List<RecipeBookGroup> groups) {
        ClientRecipeBookUtils.registerGroups(category, groups);
    }

    /**
     * Register groups for a category. Here you can specify the index for the groups.
     * <p>
     * You'll also have to call {@link RecipeBookHelper#addToSearchGroup(RecipeBookGroup, RecipeBookGroup...)} to add the groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroups(@NotNull RecipeBookCategory category, int index, @NotNull List<RecipeBookGroup> groups) {
        ClientRecipeBookUtils.registerGroups(category, index, groups);
    }

    //
    // Add to search.
    //

    /**
     * Add groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(@NotNull RecipeBookGroup searchGroup, RecipeBookGroup... groups) {
        addToSearchGroup(searchGroup, Arrays.asList(groups));
    }

    /**
     * Add groups to the search group. Here you can specify the index for the groups.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(@NotNull RecipeBookGroup searchGroup, int index, RecipeBookGroup... groups) {
        addToSearchGroup(searchGroup, index, Arrays.asList(groups));
    }

    /**
     * Add groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(@NotNull RecipeBookGroup searchGroup, @NotNull List<RecipeBookGroup> groups) {
        ClientRecipeBookUtils.addToSearchGroup(searchGroup, groups);
    }

    /**
     * Add groups to the search group. Here you can specify the index for the groups.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(@NotNull RecipeBookGroup searchGroup, int index, @NotNull List<RecipeBookGroup> groups) {
        ClientRecipeBookUtils.addToSearchGroup(searchGroup, index, groups);
    }

    //
    // Register and add to search.
    //

    /**
     * Register and add to search.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(@NotNull RecipeBookCategory category, @NotNull RecipeBookGroup searchGroup, RecipeBookGroup... groups) {
        registerAndAddToSearch(category, searchGroup, Arrays.asList(groups));
    }

    /**
     * Register and add to search. Here you can specify the index for the groups.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(@NotNull RecipeBookCategory category, @NotNull RecipeBookGroup searchGroup, int index, RecipeBookGroup... groups) {
        registerAndAddToSearch(category, searchGroup, index, Arrays.asList(groups));
    }

    /**
     * Register and add to search.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(@NotNull RecipeBookCategory category, @NotNull RecipeBookGroup searchGroup, @NotNull List<RecipeBookGroup> groups) {
        ClientRecipeBookUtils.registerGroups(category, groups);
        groups.remove(searchGroup);
        ClientRecipeBookUtils.addToSearchGroup(searchGroup, groups);
    }

    /**
     * Register and add to search. Here you can specify the index for the groups.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(@NotNull RecipeBookCategory category, @NotNull RecipeBookGroup searchGroup, int index, @NotNull List<RecipeBookGroup> groups) {
        ClientRecipeBookUtils.registerGroups(category, index, groups);
        groups.remove(searchGroup);
        ClientRecipeBookUtils.addToSearchGroup(searchGroup, Math.max(index - 1, 0), groups);
    }

    //
    // Creating groups and categories
    //

    /**
     * Creates a {@link RecipeBookCategory}. This is meant to be used in the main init of your mod.
     */
    public static RecipeBookCategory createCategory(@NotNull Identifier id) {
        return RecipeBookUtils.createCategory(id.toString().replace('/', '_').replace(':', '_'));
    }

    /**
     * Creates a {@link RecipeBookGroup}. This is meant to be used in the client init of your mod.
     * @param stacks This is the icon displayed in the recipe book. Values above 2 do not work.
     */
    @Environment(EnvType.CLIENT)
    public static RecipeBookGroup createGroup(@NotNull Identifier id, ItemStack... stacks) {
        return ClientRecipeBookUtils.createGroup(id.toString().replace('/', '_').replace(':', '_'), stacks);
    }

    @Environment(EnvType.CLIENT)
    public static boolean isSearchGroup(@NotNull RecipeBookGroup group) {
        return RecipeBookGroup.SEARCH_MAP.containsKey(group);
    }
}
