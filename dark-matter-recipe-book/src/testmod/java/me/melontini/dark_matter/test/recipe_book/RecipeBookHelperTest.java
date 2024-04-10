package me.melontini.dark_matter.test.recipe_book;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Identifier;

public class RecipeBookHelperTest implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RecipeBookCategory category = RecipeBookHelper.createCategory(new Identifier("dark-matter", "test_category"));
        MakeSure.notNull(category);

        RecipeBookGroup search = RecipeBookHelper.createGroup(new Identifier("dark-matter", "test_search_group"), Items.COMPASS.getDefaultStack());
        MakeSure.notNull(search);
        MakeSure.isTrue(search.getIcons().size() == 1);

        try {
            RecipeBookGroup group = RecipeBookHelper.createGroup(new Identifier("dark-matter", "test_group"));
            throw new IllegalStateException();
        } catch (IllegalArgumentException e) {

        }

        RecipeBookGroup group = RecipeBookHelper.createGroup(new Identifier("dark-matter", "test_group"), Items.ACACIA_DOOR.getDefaultStack());
        MakeSure.notNull(group);

        RecipeBookHelper.registerAndAddToSearch(category, search, group);
        MakeSure.isTrue(RecipeBookHelper.isSearchGroup(search));
    }
}