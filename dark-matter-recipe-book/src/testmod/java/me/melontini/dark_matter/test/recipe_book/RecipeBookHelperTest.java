package me.melontini.dark_matter.test.recipe_book;

import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Identifier;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;

public class RecipeBookHelperTest implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    RecipeBookCategory category =
        RecipeBookHelper.createCategory(Identifier.of("dark-matter", "test_category"));
    Assertions.assertThat(category).isNotNull();

    RecipeBookGroup search = RecipeBookHelper.createGroup(
        Identifier.of("dark-matter", "test_search_group"), Items.COMPASS.getDefaultStack());
    Assertions.assertThat(search)
        .isNotNull()
        .extracting(
            group -> group.getIcons().size(), Assertions.as(InstanceOfAssertFactories.INTEGER))
        .isEqualTo(1);

    Assertions.assertThatThrownBy(() -> {
          RecipeBookHelper.createGroup(Identifier.of("dark-matter", "test_group"));
          throw new AssertionError("This shouldn't have happened!");
        })
        .isInstanceOf(IllegalArgumentException.class);

    RecipeBookGroup group = RecipeBookHelper.createGroup(
        Identifier.of("dark-matter", "test_group"), Items.ACACIA_DOOR.getDefaultStack());
    Assertions.assertThat(group).isNotNull();

    RecipeBookHelper.registerAndAddToSearch(category, search, group);
    Assertions.assertThat(search)
        .matches(RecipeBookHelper::isSearchGroup, "search is a search group");
  }
}
