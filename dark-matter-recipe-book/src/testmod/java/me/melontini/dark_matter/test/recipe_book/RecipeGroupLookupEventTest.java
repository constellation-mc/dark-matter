package me.melontini.dark_matter.test.recipe_book;

import me.melontini.dark_matter.api.base.util.MakeSure;
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

public class RecipeGroupLookupEventTest implements ClientModInitializer, ClientTestEntrypoint {

    @Override
    public void onInitializeClient() {
        RecipeBookGroup group = RecipeBookHelper.createGroup(new Identifier("dark-matter", "test_event_group"), Items.CHICKEN.getDefaultStack());
        RecipeBookHelper.registerGroups(RecipeBookCategory.CRAFTING, 1, group);

        RecipeGroupLookupEvent.forType(RecipeType.CRAFTING).register((id, recipe, registryManager) -> {
            if (recipe.getOutput(registryManager).isIn(ItemTags.PLANKS)) {
                return group;
            }
            return null;
        });
    }

    @Override
    public void onClientTest(ClientTestContext context) {
        RecipeBookGroup group = EnumUtils.getEnumConstant(new Identifier("dark-matter", "test_event_group").toString().replace('/', '_').replace(':', '_'), RecipeBookGroup.class);

        MakeSure.isTrue(RecipeBookGroup.getGroups(RecipeBookCategory.CRAFTING).contains(group));

        context.submitAndWait(client -> client.player.getRecipeBook()
                .getResultsForGroup(group)
                .stream().filter(rrc -> rrc.getAllRecipes().stream()
                        .anyMatch(recipe -> recipe.getOutput(client.world.getRegistryManager())
                                .isIn(ItemTags.PLANKS)))).findFirst().orElseThrow();
    }
}
