package me.melontini.dark_matter.impl.content.mixin.item_group_builder;

import com.llamalad7.mixinextras.sugar.Local;
import me.melontini.dark_matter.api.content.ItemGroupHelper;
import me.melontini.dark_matter.impl.content.ItemGroupInjectionInternals;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemGroup.class)
public class ItemGroupMixin {

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemGroup;displayStacks:Ljava/util/Collection;", shift = At.Shift.BEFORE), method = "updateEntries")
    private void dark_matter$injectEntries(FeatureSet enabledFeatures, boolean operatorEnabled, CallbackInfo ci, @Local ItemGroup.EntriesImpl entriesImpl) {
        ItemGroupInjectionInternals.getItemGroupInjections((ItemGroup) (Object) this)
                .ifPresent(injectEntries -> injectEntries.forEach(entryInjector -> entryInjector.inject(enabledFeatures, operatorEnabled, entriesImpl)));
    }
}