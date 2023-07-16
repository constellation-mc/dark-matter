package me.melontini.dark_matter.content.mixin.item_group_builder;

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

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup$EntryCollector;accept(Lnet/minecraft/item/ItemGroup$DisplayContext;Lnet/minecraft/item/ItemGroup$Entries;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, method = "updateEntries")
    private void cracker_util$injectEntries(ItemGroup.DisplayContext displayContext, CallbackInfo ci, ItemGroup.EntriesImpl entriesImpl) {
        if (ItemGroupHelper.INJECTED_GROUPS.containsKey((ItemGroup) (Object) this)) {
            for (ItemGroupHelper.InjectEntries injectEntries : ItemGroupHelper.INJECTED_GROUPS.get((ItemGroup) (Object) this)) {
                injectEntries.inject(displayContext.enabledFeatures(), displayContext.hasPermissions(), entriesImpl);
            }
        }
    }
}