package me.melontini.dark_matter.test.item_group;

import me.melontini.dark_matter.api.item_group.ItemGroupAnimaton;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;

public class GroupAnimationTest implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        var stack = Items.SPRUCE_SIGN.getDefaultStack();
        ItemGroupAnimaton.setIconAnimation(GroupBuilderTest.group, (group, context, itemX, itemY, selected, isTopRow) -> {
            context.getMatrices().push();
            context.getMatrices().multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(Util.getMeasuringTimeMs() * 0.05f));
            context.drawItem(stack, itemX, itemY);
            context.getMatrices().pop();
        });
    }
}
