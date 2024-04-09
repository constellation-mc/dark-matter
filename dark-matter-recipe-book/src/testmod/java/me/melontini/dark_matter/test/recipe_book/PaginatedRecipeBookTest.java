package me.melontini.dark_matter.test.recipe_book;

import me.melontini.dark_matter.impl.minecraft.util.test.DarkMatterClientTest;
import me.melontini.dark_matter.impl.minecraft.util.test.FabricClientTestHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class PaginatedRecipeBookTest implements DarkMatterClientTest {
    @Override
    public void onDarkMatterClientTest() {
        FabricClientTestHelper.submitAndWait(client -> {
            client.player.networkHandler.sendCommand("gamemode survival");
            client.player.networkHandler.sendCommand("recipe give @s *");
            return null;
        });
        FabricClientTestHelper.openInventory();
        FabricClientTestHelper.waitForWorldTicks(40);

        FabricClientTestHelper.submitAndWait(client -> {
            if (client.currentScreen instanceof InventoryScreen screen) {
                if (!screen.getRecipeBookWidget().isOpen()) {
                    ((InventoryScreenAccessor) screen).dark_matter$pressRecipeBookButton();
                }
                return null;
            }
            throw new IllegalStateException();
        });
        FabricClientTestHelper.takeScreenshot("recipe-book-open");
        FabricClientTestHelper.submitAndWait(client -> {
            if (client.currentScreen instanceof InventoryScreen screen) {
                if (screen.getRecipeBookWidget().isOpen()) {
                    ((InventoryScreenAccessor) screen).dark_matter$pressRecipeBookButton();
                }
                return null;
            }
            throw new IllegalStateException();
        });
        FabricClientTestHelper.closeScreen();
        FabricClientTestHelper.submitAndWait(client -> client.player.networkHandler.sendCommand("gamemode creative"));
        FabricClientTestHelper.waitForWorldTicks(20);
    }
}
