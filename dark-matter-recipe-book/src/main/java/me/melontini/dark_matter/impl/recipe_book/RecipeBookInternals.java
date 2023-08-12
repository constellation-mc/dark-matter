package me.melontini.dark_matter.impl.recipe_book;

import com.mojang.datafixers.util.Pair;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookOptions;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class RecipeBookInternals {
    private RecipeBookInternals() {
        throw new UnsupportedOperationException();
    }
    @Environment(EnvType.CLIENT)
    private static final Map<RecipeType<?>, List<Function<Recipe<?>, RecipeBookGroup>>> TYPE_HANDLERS = new HashMap<>();
    @Environment(EnvType.CLIENT)
    private static final Map<RecipeBookCategory, List<RecipeBookGroup>> CATEGORY_TO_LIST = new HashMap<>();
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
    public static void addRecipePredicate(RecipeType<?> type, Function<Recipe<?>, RecipeBookGroup> function) {
        var list = TYPE_HANDLERS.computeIfAbsent(type, type1 -> new ArrayList<>(1));

        if (!list.contains(function)) {
            list.add(function);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, RecipeBookGroup group) {
        MakeSure.notNull(group, "Null group provided.");
        if (isVanillaCategory(category)) {
            getGroupsForCategory(category).add(group);
            return;
        }

        CATEGORY_TO_LIST.computeIfAbsent(category, category1 -> new ArrayList<>(1)).add(group);
    }

    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, int index, RecipeBookGroup group) {
        MakeSure.notNull(group, "Null group provided.");
        MakeSure.isFalse(index < 0, "Index can't be below 0!");
        if (isVanillaCategory(category)) {
            getGroupsForCategory(category).add(index, group);
            return;
        }

        if (CATEGORY_TO_LIST.containsKey(category)) {
            CATEGORY_TO_LIST.get(category).add(index, group);
        } else {
            CATEGORY_TO_LIST.computeIfAbsent(category, category1 -> new ArrayList<>(1)).add(group);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, List<RecipeBookGroup> groups) {
        if (isVanillaCategory(category)) {
            getGroupsForCategory(category).addAll(groups);
            return;
        }

        CATEGORY_TO_LIST.computeIfAbsent(category, category1 -> new ArrayList<>(groups.size())).addAll(groups);
    }

    @Environment(EnvType.CLIENT)
    public static void addToGetGroups(RecipeBookCategory category, int index, List<RecipeBookGroup> groups) {
        MakeSure.isFalse(index < 0, "Index can't be below 0!");
        if (isVanillaCategory(category)) {
            getGroupsForCategory(category).addAll(index, groups);
            return;
        }

        if (CATEGORY_TO_LIST.containsKey(category)) {
            CATEGORY_TO_LIST.get(category).addAll(index, groups);
        } else {
            CATEGORY_TO_LIST.computeIfAbsent(category, category1 -> new ArrayList<>(groups.size())).addAll(groups);
        }
    }

    @Environment(EnvType.CLIENT)
    public static void addToSearchMap(RecipeBookGroup searchGroup, List<RecipeBookGroup> groups) {
        RecipeBookGroup.SEARCH_MAP.computeIfAbsent(MakeSure.notNull(searchGroup), group -> new ArrayList<>()).addAll(MakeSure.notEmpty(groups));
    }

    @Environment(EnvType.CLIENT)
    public static void addToSearchMap(RecipeBookGroup searchGroup, int index, List<RecipeBookGroup> groups) {
        if (RecipeBookGroup.SEARCH_MAP.containsKey(searchGroup)) {
            RecipeBookGroup.SEARCH_MAP.get(MakeSure.notNull(searchGroup)).addAll(index, MakeSure.notEmpty(groups));
        } else {
            RecipeBookGroup.SEARCH_MAP.computeIfAbsent(MakeSure.notNull(searchGroup), group -> new ArrayList<>()).addAll(MakeSure.notEmpty(groups));
        }
    }

    public static RecipeBookCategory createCategory(String internalName) {
        MakeSure.notEmpty(internalName, "Tried to register a RecipeBookCategory with an empty string.");

        RecipeBookCategory category = (RecipeBookCategory) RecipeBookCategory.values()[0].dark_matter$extend(internalName);

        RecipeBookOptions.CATEGORY_OPTION_NAMES.putIfAbsent(category, new Pair<>("is" + internalName + "GuiOpen", "is" + internalName + "FilteringCraftable"));
        return category;
    }

    @Environment(EnvType.CLIENT)
    public static boolean hasHandlers(RecipeType<?> type) {
        return TYPE_HANDLERS.containsKey(type);
    }

    @Environment(EnvType.CLIENT)
    public static List<Function<Recipe<?>, RecipeBookGroup>> getHandlers(RecipeType<?> type) {
        return Collections.unmodifiableList(TYPE_HANDLERS.get(type));
    }

    @Environment(EnvType.CLIENT)
    public static boolean hasGroups(RecipeBookCategory category) {
        return CATEGORY_TO_LIST.containsKey(category);
    }

    @Environment(EnvType.CLIENT)
    public static List<RecipeBookGroup> getGroups(RecipeBookCategory category) {
        return Collections.unmodifiableList(CATEGORY_TO_LIST.get(category));
    }
}
