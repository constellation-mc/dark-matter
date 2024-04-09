package me.melontini.dark_matter.test.item_group;

import me.melontini.dark_matter.api.item_group.ItemGroupAnimaton;
import me.melontini.dark_matter.api.minecraft.client.util.DrawUtil;
import me.melontini.dark_matter.impl.minecraft.util.test.DarkMatterClientTest;
import me.melontini.dark_matter.impl.minecraft.util.test.FabricClientTestHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;

public class GroupAnimationTest implements ClientModInitializer, DarkMatterClientTest {
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
    public void onDarkMatterClientTest() {
        FabricClientTestHelper.setScreen(client -> new CreativeInventoryScreen(client.player, client.world.getEnabledFeatures(), false));
        FabricClientTestHelper.waitForWorldTicks(10);
        FabricClientTestHelper.submitAndWait(client -> {
            ((CreativeGuiExtensions)client.currentScreen).fabric_nextPage();
            return null;
        });
        FabricClientTestHelper.takeScreenshot("item-group-animation");
        FabricClientTestHelper.closeScreen();
    }
}
