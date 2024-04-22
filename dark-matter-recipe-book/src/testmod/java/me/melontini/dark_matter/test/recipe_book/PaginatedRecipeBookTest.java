package me.melontini.dark_matter.test.recipe_book;

import me.melontini.handytests.client.ClientTestContext;
import me.melontini.handytests.client.ClientTestEntrypoint;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class PaginatedRecipeBookTest implements ClientTestEntrypoint {

    @Override
    public void onClientTest(ClientTestContext context) {
        context.sendCommand("gamemode survival");
        context.sendCommand("recipe give @s *");
        context.openInventory();
        context.waitForWorldTicks(40);

        context.executeForScreen(InventoryScreen.class, (client, screen) -> {
            if (!screen.getRecipeBookWidget().isOpen()) {
                ((InventoryScreenAccessor) screen).dark_matter$pressRecipeBookButton();
            }
            return null;
        });
        context.takeScreenshot("recipe-book-open");
        context.executeForScreen(InventoryScreen.class, (client, screen) -> {
            if (screen.getRecipeBookWidget().isOpen()) {
                ((InventoryScreenAccessor) screen).dark_matter$pressRecipeBookButton();
            }
            return null;
        });
        context.closeScreen();
        context.sendCommand("gamemode creative");
        context.waitForWorldTicks(20);
    }
}
