package me.melontini.dark_matter.api.item_group;

import me.melontini.dark_matter.impl.item_group.ItemGroupExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemGroup;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface ItemGroupAnimaton {

    static ItemGroup setIconAnimation(ItemGroup group, ItemGroupAnimaton animation) {
        ((ItemGroupExtensions)group).dm$setIconAnimation(animation);
        return group;
    }

    /**
     * Animates the icon for your item group.
     *
     * <p>This can draw anything you want</p>
     *
     * @param context the matrix stack used to render the screen
     * @param itemX the x-coordinate of the icon
     * @param itemY the y-coordinate of the icon
     */
    void animateIcon(ItemGroup group, DrawContext context, int itemX, int itemY, boolean selected, boolean isTopRow);
}
