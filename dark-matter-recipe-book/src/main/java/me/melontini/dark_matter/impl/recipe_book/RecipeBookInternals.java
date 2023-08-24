package me.melontini.dark_matter.impl.recipe_book;

import com.mojang.datafixers.util.Pair;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.enums.EnumWrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookOptions;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class RecipeBookInternals {

    private RecipeBookInternals() {
        throw new UnsupportedOperationException();
    }

    @Environment(EnvType.CLIENT)
    private static final Map<RecipeType<?>, List<Function<Recipe<?>, RecipeBookGroup>>> GROUP_LOOKUPS = new HashMap<>();

    @Environment(EnvType.CLIENT)
    private static final Map<RecipeBookCategory, List<RecipeBookGroup>> GROUPS_FOR_CATEGORY = new HashMap<>();

    @Environment(EnvType.CLIENT)
    private static final Map<RecipeBookCategory, Supplier<List<RecipeBookGroup>>> VANILLA_CATEGORIES = Utilities.consume(new HashMap<>(), map -> {
        map.put(RecipeBookCategory.CRAFTING, () -> RecipeBookGroup.CRAFTING);
        map.put(RecipeBookCategory.FURNACE, () -> RecipeBookGroup.FURNACE);
        map.put(RecipeBookCategory.BLAST_FURNACE, () -> RecipeBookGroup.BLAST_FURNACE);
        map.put(RecipeBookCategory.SMOKER, () -> RecipeBookGroup.SMOKER);
    });

    @Environment(EnvType.CLIENT)
    private static boolean isVanillaCategory(RecipeBookCategory category) {
        return VANILLA_CATEGORIES.containsKey(category);
    }

    @Environment(EnvType.CLIENT)
    private static List<RecipeBookGroup> getGroupsForCategory(RecipeBookCategory category) {
        return VANILLA_CATEGORIES.get(category).get();
    }

    @Environment(EnvType.CLIENT)
    public static void registerGroupLookup(RecipeType<?> type, Function<Recipe<?>, RecipeBookGroup> function) {
        var list = GROUP_LOOKUPS.computeIfAbsent(type, type1 -> new ArrayList<>(1));

        if (!list.contains(function)) {
            list.add(function);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void registerGroups(RecipeBookCategory category, List<RecipeBookGroup> groups) {
        if (isVanillaCategory(category)) {
            getGroupsForCategory(category).addAll(groups);
            return;
        }

        GROUPS_FOR_CATEGORY.computeIfAbsent(category, category1 -> new ArrayList<>(groups.size())).addAll(groups);
    }

    @Environment(EnvType.CLIENT)
    public static void registerGroups(RecipeBookCategory category, int index, List<RecipeBookGroup> groups) {
        MakeSure.isFalse(index < 0, "Index can't be below 0!");
        if (isVanillaCategory(category)) {
            getGroupsForCategory(category).addAll(index, groups);
            return;
        }

        if (GROUPS_FOR_CATEGORY.containsKey(category)) {
            GROUPS_FOR_CATEGORY.get(category).addAll(index, groups);
        } else {
            GROUPS_FOR_CATEGORY.computeIfAbsent(category, category1 -> new ArrayList<>(groups.size())).addAll(groups);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(RecipeBookGroup searchGroup, List<RecipeBookGroup> groups) {
        RecipeBookGroup.SEARCH_MAP.computeIfAbsent(MakeSure.notNull(searchGroup), group -> new ArrayList<>()).addAll(MakeSure.notEmpty(groups));
    }

    @Environment(EnvType.CLIENT)
    public static void addToSearchGroup(RecipeBookGroup searchGroup, int index, List<RecipeBookGroup> groups) {
        if (RecipeBookGroup.SEARCH_MAP.containsKey(searchGroup)) {
            RecipeBookGroup.SEARCH_MAP.get(MakeSure.notNull(searchGroup)).addAll(index, MakeSure.notEmpty(groups));
        } else {
            RecipeBookGroup.SEARCH_MAP.computeIfAbsent(MakeSure.notNull(searchGroup), group -> new ArrayList<>()).addAll(MakeSure.notEmpty(groups));
        }
    }

    public static RecipeBookCategory createCategory(String internalName) {
        MakeSure.notEmpty(internalName, "Tried to create a RecipeBookCategory with an empty string.");

        RecipeBookCategory category = (RecipeBookCategory) RecipeBookCategory.values()[0].dark_matter$extend(internalName);

        RecipeBookOptions.CATEGORY_OPTION_NAMES.putIfAbsent(category, new Pair<>("is" + internalName + "GuiOpen", "is" + internalName + "FilteringCraftable"));
        return category;
    }

    @Environment(EnvType.CLIENT)
    public static RecipeBookGroup createGroup(String internalName, ItemStack... stacks) {
        MakeSure.notEmpty(internalName, "Tried to create a RecipeBookGroup with an empty string.");

        return EnumWrapper.RecipeBookGroup.extend(internalName, stacks);
    }

    @Environment(EnvType.CLIENT)
    public static Optional<List<Function<Recipe<?>, RecipeBookGroup>>> getLookups(RecipeType<?> type) {
        return GROUP_LOOKUPS.containsKey(type) ? Optional.of(Collections.unmodifiableList(GROUP_LOOKUPS.get(type))) : Optional.empty();
    }

    @Environment(EnvType.CLIENT)
    public static Optional<List<RecipeBookGroup>> getGroups(RecipeBookCategory category) {
        return GROUPS_FOR_CATEGORY.containsKey(category) ? Optional.of(Collections.unmodifiableList(GROUPS_FOR_CATEGORY.get(category))) : Optional.empty();
    }

}
