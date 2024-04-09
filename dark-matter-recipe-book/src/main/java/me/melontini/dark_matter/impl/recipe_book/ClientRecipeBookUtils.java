package me.melontini.dark_matter.impl.recipe_book;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.api.recipe_book.events.RecipeGroupLookupEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;

import java.util.*;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@UtilityClass
public class ClientRecipeBookUtils {

    private static final Map<RecipeBookCategory, List<RecipeBookGroup>> GROUPS_FOR_CATEGORY = new IdentityHashMap<>();

    private static final Map<RecipeBookCategory, Supplier<List<RecipeBookGroup>>> VANILLA_CATEGORIES = Utilities.supply(new IdentityHashMap<>(), map -> {
        map.put(RecipeBookCategory.CRAFTING, () -> RecipeBookGroup.CRAFTING);
        map.put(RecipeBookCategory.FURNACE, () -> RecipeBookGroup.FURNACE);
        map.put(RecipeBookCategory.BLAST_FURNACE, () -> RecipeBookGroup.BLAST_FURNACE);
        map.put(RecipeBookCategory.SMOKER, () -> RecipeBookGroup.SMOKER);
    });

    private static final Map<RecipeType<?>, Event<?>> EVENTS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> Event<RecipeGroupLookupEvent<T>> forType(RecipeType<T> type, boolean create) {
        if (create) {
            return Utilities.cast(EVENTS.computeIfAbsent(type, type1 -> EventFactory.createArrayBacked(RecipeGroupLookupEvent.class, recipeGroupLookupEvents -> (id, recipe, registryManager) -> {
                RecipeBookGroup group;
                for (RecipeGroupLookupEvent<T> event : recipeGroupLookupEvents) {
                    if ((group = event.onRecipeBookGroupLookup(id, (T) recipe, registryManager)) != null) {
                        return group;
                    }
                }
                return null;
            })));
        } else {
            return Utilities.cast(EVENTS.get(type));
        }
    }

    private static boolean isVanillaCategory(RecipeBookCategory category) {
        return VANILLA_CATEGORIES.containsKey(category);
    }

    private static List<RecipeBookGroup> getGroupsForCategory(RecipeBookCategory category) {
        if (isVanillaCategory(category)) {
            return VANILLA_CATEGORIES.get(category).get();
        }
        return GROUPS_FOR_CATEGORY.computeIfAbsent(category, category1 -> new ArrayList<>());
    }

    public static void registerGroups(RecipeBookCategory category, List<RecipeBookGroup> groups) {
        MakeSure.notNulls(category, groups);
        getGroupsForCategory(category).addAll(groups);
    }

    public static void registerGroups(RecipeBookCategory category, int index, List<RecipeBookGroup> groups) {
        MakeSure.notNulls(category, groups);
        MakeSure.isTrue(index >= 0, "Index can't be below 0!");

        List<RecipeBookGroup> groupList = getGroupsForCategory(category);
        if (index >= groupList.size()) groupList.addAll(groups);
        else groupList.addAll(index, groups);
    }

    public static void addToSearchGroup(RecipeBookGroup searchGroup, List<RecipeBookGroup> groups) {
        MakeSure.notNulls(searchGroup, groups);
        List<RecipeBookGroup> groupList = RecipeBookGroup.SEARCH_MAP.computeIfAbsent(searchGroup, group -> new ArrayList<>());
        groupList.addAll(groups);
    }

    public static void addToSearchGroup(RecipeBookGroup searchGroup, int index, List<RecipeBookGroup> groups) {
        MakeSure.notNulls(searchGroup, groups);
        List<RecipeBookGroup> groupList = RecipeBookGroup.SEARCH_MAP.computeIfAbsent(searchGroup, group -> new ArrayList<>());

        if (index >= groupList.size()) groupList.addAll(groups);
        else groupList.addAll(index, groups);
    }

    public static RecipeBookGroup createGroup(String internalName, ItemStack... stacks) {
        MakeSure.notEmpty(internalName, "Tried to create a RecipeBookGroup with an empty string.");
        MakeSure.notEmpty(stacks, "Tried to create a RecipeBookGroup with no icons.");
        MakeSure.isTrue(stacks.length <= 2, "Tried to create a RecipeBookGroup with too many icons!");

        return ExtendableEnum.extend(RecipeBookGroup.class, internalName, () -> stacks);
    }

    public static Optional<List<RecipeBookGroup>> getGroups(RecipeBookCategory category) {
        return Optional.ofNullable(GROUPS_FOR_CATEGORY.get(category)).map(Collections::unmodifiableList);
    }
}
