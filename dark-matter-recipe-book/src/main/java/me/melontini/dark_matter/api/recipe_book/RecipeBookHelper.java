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

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class RecipeBookHelper {

    private RecipeBookHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Allows you to map a recipe to a group.
     * <p>
     * This supports adding multiple lookups for the same type. So, multiple mods can map their recipes of the same type.
     * @param lookup The lookup function. Please note, the function must return null if the recipe doesn't match.
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroupLookup(RecipeType<?> type, Function<Recipe<?>, RecipeBookGroup> lookup) {
        RecipeBookInternals.registerGroupLookup(type, lookup);
    }

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
    public static void registerGroups(RecipeBookCategory category, RecipeBookGroup... groups) {
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
    public static void registerGroups(RecipeBookCategory category, int index, RecipeBookGroup... groups) {
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
    public static void registerGroups(RecipeBookCategory category, List<RecipeBookGroup> groups) {
        RecipeBookInternals.registerGroups(category, groups);
    }

    /**
     * Register groups for a category. Here you can specify the index for the groups.
     * <p>
     * You'll also have to call {@link RecipeBookHelper#addToSearchGroup(RecipeBookGroup, RecipeBookGroup...)} to add the groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void registerGroups(RecipeBookCategory category, int index, List<RecipeBookGroup> groups) {
        RecipeBookInternals.registerGroups(category, index, groups);
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
    public static void addToSearchGroup(RecipeBookGroup searchGroup, RecipeBookGroup... groups) {
        addToSearchGroup(searchGroup, Arrays.asList(groups));
    }

    /**
     * Add groups to the search group. Here you can specify the index for the groups.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(RecipeBookGroup searchGroup, int index, RecipeBookGroup... groups) {
        addToSearchGroup(searchGroup, index, Arrays.asList(groups));
    }

    /**
     * Add groups to the search group.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(RecipeBookGroup searchGroup, List<RecipeBookGroup> groups) {
        RecipeBookInternals.addToSearchGroup(searchGroup, groups);
    }

    /**
     * Add groups to the search group. Here you can specify the index for the groups.
     * <p>
     * You can also use {@link RecipeBookHelper#registerAndAddToSearch(RecipeBookCategory, RecipeBookGroup, RecipeBookGroup...)}
     */
    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(RecipeBookGroup searchGroup, int index, List<RecipeBookGroup> groups) {
        RecipeBookInternals.addToSearchGroup(searchGroup, index, groups);
    }

    //
    // Register and add to search.
    //

    /**
     * Register and add to search.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(RecipeBookCategory category, RecipeBookGroup searchGroup, RecipeBookGroup... groups) {
        registerAndAddToSearch(category, searchGroup, Arrays.asList(groups));
    }

    /**
     * Register and add to search. Here you can specify the index for the groups.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(RecipeBookCategory category, RecipeBookGroup searchGroup, int index, RecipeBookGroup... groups) {
        registerAndAddToSearch(category, searchGroup, index, Arrays.asList(groups));
    }

    /**
     * Register and add to search.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(RecipeBookCategory category, RecipeBookGroup searchGroup, List<RecipeBookGroup> groups) {
        RecipeBookInternals.registerGroups(category, groups);
        groups.remove(searchGroup);
        RecipeBookInternals.addToSearchGroup(searchGroup, groups);
    }

    /**
     * Register and add to search. Here you can specify the index for the groups.
     */
    @Environment(EnvType.CLIENT)
    public static void registerAndAddToSearch(RecipeBookCategory category, RecipeBookGroup searchGroup, int index, List<RecipeBookGroup> groups) {
        RecipeBookInternals.registerGroups(category, index, groups);
        groups.remove(searchGroup);
        RecipeBookInternals.addToSearchGroup(searchGroup, index, groups);
    }

    //
    // Creating groups and categories
    //

    /**
     * Creates a {@link RecipeBookCategory}. This is meant to be used in the main init of your mod.
     */
    public static RecipeBookCategory createCategory(Identifier id) {
        return RecipeBookInternals.createCategory(id.toString().replace('/', '_').replace(':', '_'));
    }

    /**
     * Creates a {@link RecipeBookGroup}. This is meant to be used in the client init of your mod.
     * @param stacks This is the icon displayed in the recipe book. Values above 2 do not work.
     */
    @Environment(EnvType.CLIENT)
    public static RecipeBookGroup createGroup(Identifier id, ItemStack... stacks) {
        return RecipeBookInternals.createGroup(id.toString().replace('/', '_').replace(':', '_'), stacks);
    }

    //
    // Old, deprecated code.
    //

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addRecipePredicate(RecipeType<?> type, Function<Recipe<?>, RecipeBookGroup> function) {
        registerGroupLookup(type, function);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, RecipeBookGroup group) {
        registerGroups(category, group);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, int index, RecipeBookGroup group) {
        registerGroups(category, index, group);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, List<RecipeBookGroup> groups) {
        registerGroups(category, groups);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, int index, List<RecipeBookGroup> groups) {
        registerGroups(category, index, groups);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToSearchMap(RecipeBookGroup searchGroup, List<RecipeBookGroup> groups) {
        addToSearchGroup(searchGroup, groups);
    }

    @Deprecated(since = "2.0.0")
    @Environment(EnvType.CLIENT)
    public static void addToSearchMap(RecipeBookGroup searchGroup, int index, List<RecipeBookGroup> groups) {
        addToSearchGroup(searchGroup, index, groups);
    }

}
