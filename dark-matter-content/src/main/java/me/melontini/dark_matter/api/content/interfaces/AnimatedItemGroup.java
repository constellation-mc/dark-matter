package me.melontini.dark_matter.api.content.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;

@FunctionalInterface
public interface AnimatedItemGroup {
    /**
     * Animates the icon for your item group.
     *
     * <p>This can draw anything you want</p>
     */
    @Environment(EnvType.CLIENT)
    void animateIcon(ItemGroup group, MatrixStack matrixStack, int itemX, int itemY, boolean selected, boolean isTopRow);
}
