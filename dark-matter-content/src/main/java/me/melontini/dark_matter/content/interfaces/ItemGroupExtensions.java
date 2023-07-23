package me.melontini.dark_matter.content.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;

public interface ItemGroupExtensions {
    @Environment(EnvType.CLIENT)
    @Deprecated
    default boolean shouldAnimateIcon() {
        return dm$shouldAnimateIcon();
    }

    @Environment(EnvType.CLIENT)
    default boolean dm$shouldAnimateIcon() {
        return false;
    }

    @Deprecated
    default ItemGroup setIconAnimation(AnimatedItemGroup animation) {
        return dm$setIconAnimation(animation);
    }

    default ItemGroup dm$setIconAnimation(AnimatedItemGroup animation) {
        throw new IllegalStateException("Interface not implemented");
    }

    @Deprecated
    @Environment(EnvType.CLIENT)
    default AnimatedItemGroup getIconAnimation() {
        return dm$getIconAnimation();
    }

    @Environment(EnvType.CLIENT)
    default AnimatedItemGroup dm$getIconAnimation() {
        throw new IllegalStateException("Interface not implemented");
    }
}
