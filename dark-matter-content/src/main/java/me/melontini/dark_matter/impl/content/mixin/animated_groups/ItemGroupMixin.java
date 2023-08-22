package me.melontini.dark_matter.impl.content.mixin.animated_groups;

import me.melontini.dark_matter.api.content.interfaces.AnimatedItemGroup;
import me.melontini.dark_matter.api.content.interfaces.ItemGroupExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(ItemGroup.class)
public class ItemGroupMixin implements ItemGroupExtensions {

    @Unique
    public Supplier<AnimatedItemGroup> dark_matter$animation;

    @Environment(EnvType.CLIENT)
    @Override
    public boolean dm$shouldAnimateIcon() {
        return dark_matter$animation != null;
    }

    @Override
    public ItemGroup dm$setIconAnimation(Supplier<AnimatedItemGroup> animation) {
        this.dark_matter$animation = animation;
        return (ItemGroup) (Object) this;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public AnimatedItemGroup dm$getIconAnimation() {
        return this.dark_matter$animation.get();
    }
}

