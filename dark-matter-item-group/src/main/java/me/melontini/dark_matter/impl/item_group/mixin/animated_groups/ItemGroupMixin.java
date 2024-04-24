package me.melontini.dark_matter.impl.item_group.mixin.animated_groups;

import me.melontini.dark_matter.api.item_group.ItemGroupAnimaton;
import me.melontini.dark_matter.impl.item_group.ItemGroupExtensions;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemGroup.class)
public class ItemGroupMixin implements ItemGroupExtensions {

    @Unique
    private ItemGroupAnimaton dark_matter$animation;

    @Override
    public ItemGroup dm$setIconAnimation(ItemGroupAnimaton animation) {
        this.dark_matter$animation = animation;
        return (ItemGroup) (Object) this;
    }

    @Override
    public ItemGroupAnimaton dm$getIconAnimation() {
        return dark_matter$animation;
    }
}

