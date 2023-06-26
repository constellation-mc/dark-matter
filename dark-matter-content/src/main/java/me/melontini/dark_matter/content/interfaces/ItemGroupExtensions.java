package me.melontini.dark_matter.content.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;

public interface ItemGroupExtensions {
    @Environment(EnvType.CLIENT)
    default boolean shouldAnimateIcon() {
        return false;
    }

    default ItemGroup setIconAnimation(AnimatedItemGroup animation) {
        throw new IllegalStateException("Interface not implemented");
    }

    @Environment(EnvType.CLIENT)
    default AnimatedItemGroup getIconAnimation() {
        throw new IllegalStateException("Interface not implemented");
    }
}
