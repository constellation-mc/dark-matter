package me.melontini.dark_matter.content.mixin.animated_groups;

import me.melontini.dark_matter.content.interfaces.AnimatedItemGroup;
import me.melontini.dark_matter.content.interfaces.ItemGroupExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemGroup.class)
public class ItemGroupMixin implements ItemGroupExtensions {
    public AnimatedItemGroup cracker_util$animation;

    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldAnimateIcon() {
        return cracker_util$animation != null;
    }

    @Override
    public ItemGroup setIconAnimation(AnimatedItemGroup animation) {
        this.cracker_util$animation = animation;
        return (ItemGroup) (Object) this;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public AnimatedItemGroup getIconAnimation() {
        return this.cracker_util$animation;
    }
}