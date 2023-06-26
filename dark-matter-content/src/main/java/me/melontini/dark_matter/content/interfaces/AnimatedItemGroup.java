package me.melontini.dark_matter.content.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@FunctionalInterface
public interface AnimatedItemGroup {
    /**
     * Animates the icon for your item group.
     *
     * <p>This can draw anything you want</p>
     *
     * @param matrixStack the matrix stack used to render the screen
     * @param itemX the x-coordinate of the icon
     * @param itemY the y-coordinate of the icon
     */
    @Environment(EnvType.CLIENT)
    void animateIcon(MatrixStack matrixStack, int itemX, int itemY, boolean selected, boolean isTopRow);
}
