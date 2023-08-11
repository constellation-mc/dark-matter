package me.melontini.dark_matter.impl.content.mixin.item_group_builder;

import me.melontini.dark_matter.content.ItemGroupHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemGroup.class)
public class ItemGroupMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup$EntryCollector;accept(Lnet/minecraft/resource/featuretoggle/FeatureSet;Lnet/minecraft/item/ItemGroup$Entries;Z)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, method = "updateEntries", require = 0)
    private void dark_matter$injectEntries(FeatureSet enabledFeatures, boolean operatorEnabled, CallbackInfo ci, ItemGroup.EntriesImpl entriesImpl) {
        if (ItemGroupHelper.INJECTED_GROUPS.containsKey((ItemGroup) (Object) this)) {
            for (ItemGroupHelper.InjectEntries injectEntries : ItemGroupHelper.INJECTED_GROUPS.get((ItemGroup) (Object) this)) {
                injectEntries.inject(enabledFeatures, operatorEnabled, entriesImpl);
            }
        }
    }
}