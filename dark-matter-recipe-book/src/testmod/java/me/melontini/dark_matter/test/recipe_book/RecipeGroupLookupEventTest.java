package me.melontini.dark_matter.test.recipe_book;

import me.melontini.dark_matter.api.enums.EnumUtils;
import me.melontini.dark_matter.api.recipe_book.RecipeBookHelper;
import me.melontini.dark_matter.api.recipe_book.events.RecipeGroupLookupEvent;
import me.melontini.handytests.client.ClientTestContext;
import me.melontini.handytests.client.ClientTestEntrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.assertj.core.api.Assertions;

public class RecipeGroupLookupEventTest implements ClientModInitializer, ClientTestEntrypoint {

  @Override
  public void onInitializeClient() {
    RecipeBookGroup group = RecipeBookHelper.createGroup(
        Identifier.of("dark-matter", "test_event_group"), Items.CHICKEN.getDefaultStack());
    RecipeBookHelper.registerAndAddToSearch(
        RecipeBookCategory.CRAFTING, RecipeBookGroup.CRAFTING_SEARCH, 1, group);

    RecipeBookGroup group1 = RecipeBookHelper.createGroup(
        Identifier.of("dark-matter", "test_event_group_2"), Items.CACTUS.getDefaultStack());
    RecipeBookHelper.registerAndAddToSearch(
        RecipeBookCategory.CRAFTING, RecipeBookGroup.CRAFTING_SEARCH, group);

    RecipeGroupLookupEvent.forType(RecipeType.CRAFTING).register((id, recipe, registryManager) -> {
      var o = recipe.getResult(registryManager);
      if (o.isIn(ItemTags.PLANKS)) return group;
      if (o.isIn(ItemTags.LOGS)) return group1;
      return null;
    });
  }

  @Override
  public void onClientTest(ClientTestContext context) {
    RecipeBookGroup group = EnumUtils.getEnumConstant(
        Identifier.of("dark-matter", "test_event_group")
            .toString()
            .replace('/', '_')
            .replace(':', '_'),
        RecipeBookGroup.class);

    Assertions.assertThat(RecipeBookGroup.getGroups(RecipeBookCategory.CRAFTING).contains(group))
        .isTrue();

    context
        .submitAndWait(client -> client.player.getRecipeBook().getResultsForGroup(group).stream()
            .filter(rrc -> rrc.getAllRecipes().stream().anyMatch(recipe -> recipe
                .value()
                .getResult(client.world.getRegistryManager())
                .isIn(ItemTags.PLANKS))))
        .findFirst()
        .orElseThrow();
  }
}
