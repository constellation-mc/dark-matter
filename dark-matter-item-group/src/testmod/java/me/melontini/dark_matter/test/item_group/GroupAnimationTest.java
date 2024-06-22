package me.melontini.dark_matter.test.item_group;

import me.melontini.dark_matter.api.item_group.ItemGroupAnimaton;
import me.melontini.dark_matter.api.minecraft.client.util.DrawUtil;
import me.melontini.handytests.client.ClientTestContext;
import me.melontini.handytests.client.ClientTestEntrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;

public class GroupAnimationTest implements ClientModInitializer, ClientTestEntrypoint {
    @Override
    public void onInitializeClient() {
        var stack = Items.SPRUCE_SIGN.getDefaultStack();
        ItemGroupAnimaton.setIconAnimation(GroupBuilderTest.group, (group, context, itemX, itemY, selected, isTopRow) -> {
            BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModel(stack, null, null, 0);
            context.getMatrices().push();
            context.getMatrices().translate(itemX, itemY, 100);
            context.getMatrices().translate(8.0, 8.0, 0.0);
            context.getMatrices().scale(1.0F, -1.0F, 1.0F);
            context.getMatrices().scale(15.0F, 15.0F, 15.0F);

            context.getMatrices().multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(Util.getMeasuringTimeMs() * 0.05f));
            DrawUtil.renderGuiItemModelCustomMatrixNoTransform(context.getMatrices(), stack, model);
            context.getMatrices().pop();
        });
    }

    @Override
    public void onClientTest(ClientTestContext context) {
        context.setScreen(client -> new CreativeInventoryScreen(client.player, client.world.getEnabledFeatures(), false));
        context.waitForWorldTicks(10);
        context.executeForScreen(CreativeInventoryScreen.class, (client, screen) -> {
            ((FabricCreativeInventoryScreen) screen).switchToNextPage();
            return null;
        });
        context.takeScreenshot("item-group-animation");
        context.closeScreen();
    }
}
