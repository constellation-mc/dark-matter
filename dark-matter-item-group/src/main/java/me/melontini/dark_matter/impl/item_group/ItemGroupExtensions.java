package me.melontini.dark_matter.impl.item_group;

import me.melontini.dark_matter.api.item_group.ItemGroupAnimaton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;

public interface ItemGroupExtensions {

    @Environment(EnvType.CLIENT)
    default ItemGroup dm$setIconAnimation(ItemGroupAnimaton animation) {
        throw new IllegalStateException("Interface not implemented");
    }

    @Environment(EnvType.CLIENT)
    default ItemGroupAnimaton dm$getIconAnimation() {
        throw new IllegalStateException("Interface not implemented");
    }
}
