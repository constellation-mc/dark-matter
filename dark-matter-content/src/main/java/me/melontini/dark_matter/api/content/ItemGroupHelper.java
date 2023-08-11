package me.melontini.dark_matter.api.content;

import me.melontini.dark_matter.impl.content.ItemGroupInjectionInternals;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class ItemGroupHelper {

    public static void addItemGroupInjection(ItemGroup group, InjectEntries injectEntries) {
        ItemGroupInjectionInternals.addItemGroupInjection(group, injectEntries);
    }

    @FunctionalInterface
    public interface InjectEntries {
        void inject(FeatureSet enabledFeatures, boolean operatorEnabled, ItemGroup.Entries entriesImpl);
    }
}
