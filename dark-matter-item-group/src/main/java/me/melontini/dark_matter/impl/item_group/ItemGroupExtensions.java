package me.melontini.dark_matter.impl.item_group;

import me.melontini.dark_matter.api.item_group.ItemGroupAnimaton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;

@Environment(EnvType.CLIENT)
public interface ItemGroupExtensions {

  default ItemGroup dm$setIconAnimation(ItemGroupAnimaton animation) {
    throw new IllegalStateException("Interface not implemented");
  }

  default ItemGroupAnimaton dm$getIconAnimation() {
    throw new IllegalStateException("Interface not implemented");
  }
}
