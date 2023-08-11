package me.melontini.dark_matter.impl.content.mixin.item_group_builder;

import me.melontini.dark_matter.api.content.ItemGroupHelper;
import me.melontini.dark_matter.impl.content.ItemGroupInjectionInternals;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemGroup.class)
public class ItemGroupMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup$EntryCollector;accept(Lnet/minecraft/item/ItemGroup$DisplayContext;Lnet/minecraft/item/ItemGroup$Entries;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, method = "updateEntries", require = 0)
    private void dark_matter$injectEntries(ItemGroup.DisplayContext displayContext, CallbackInfo ci, ItemGroup.EntriesImpl entriesImpl) {
        if (ItemGroupInjectionInternals.INJECTED_GROUPS.containsKey((ItemGroup) (Object) this)) {
            for (ItemGroupHelper.InjectEntries injectEntries : ItemGroupInjectionInternals.INJECTED_GROUPS.get((ItemGroup) (Object) this)) {
                injectEntries.inject(displayContext.enabledFeatures(), displayContext.hasPermissions(), entriesImpl);
            }
        }
    }
}