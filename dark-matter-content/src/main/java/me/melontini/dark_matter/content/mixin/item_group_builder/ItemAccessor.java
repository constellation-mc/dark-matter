package me.melontini.dark_matter.content.mixin.item_group_builder;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {
    @Accessor("group")
    void dark_matter$setGroup(ItemGroup group);
}
