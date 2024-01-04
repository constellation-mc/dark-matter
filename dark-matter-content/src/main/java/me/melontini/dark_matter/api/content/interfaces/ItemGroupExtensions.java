package me.melontini.dark_matter.api.content.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;

import java.util.function.Supplier;

public interface ItemGroupExtensions {

    @Environment(EnvType.CLIENT)
    default boolean dm$shouldAnimateIcon() {
        return false;
    }

    default ItemGroup dm$setIconAnimation(AnimatedItemGroup animation) {
        return this.dm$setIconAnimation(() -> animation);
    }

    default ItemGroup dm$setIconAnimation(Supplier<AnimatedItemGroup> animation) {
        throw new IllegalStateException("Interface not implemented");
    }

    @Environment(EnvType.CLIENT)
    default AnimatedItemGroup dm$getIconAnimation() {
        throw new IllegalStateException("Interface not implemented");
    }
}
