package me.melontini.dark_matter.content.mixin.item_group_builder;

import me.melontini.dark_matter.content.interfaces.internal.ItemGroupArrayExtender;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemGroup.class)
public abstract class ItemGroupMixin implements ItemGroupArrayExtender {
    @Shadow
    @Final
    @Mutable
    public static ItemGroup[] GROUPS;

    @Override
    public void dark_matter$crack_array() {
        ItemGroup[] tempGroups = GROUPS;
        GROUPS = new ItemGroup[GROUPS.length + 1];

        System.arraycopy(tempGroups, 0, GROUPS, 0, tempGroups.length);
    }
}
