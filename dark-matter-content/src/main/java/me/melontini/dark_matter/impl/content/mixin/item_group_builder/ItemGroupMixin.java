package me.melontini.dark_matter.impl.content.mixin.item_group_builder;

import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.dark_matter.api.content.ItemGroupHelper;
import me.melontini.dark_matter.impl.content.ItemGroupInjectionInternals;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemGroup.class)
public class ItemGroupMixin {

    @Inject(at = @At(value = "NEW", target = "(Lnet/minecraft/item/ItemGroup;Lnet/minecraft/resource/featuretoggle/FeatureSet;)Lnet/minecraft/item/ItemGroup$EntriesImpl;", shift = At.Shift.AFTER), method = "updateEntries", require = 0)
    private void dark_matter$injectEntries(ItemGroup.DisplayContext displayContext, CallbackInfo ci, @Local ItemGroup.EntriesImpl entriesImpl) {
        if (ItemGroupInjectionInternals.INJECTED_GROUPS.containsKey((ItemGroup) (Object) this)) {
            for (ItemGroupHelper.InjectEntries injectEntries : ItemGroupInjectionInternals.INJECTED_GROUPS.get((ItemGroup) (Object) this)) {
                injectEntries.inject(displayContext.enabledFeatures(), displayContext.hasPermissions(), entriesImpl);
            }
        }
    }
}