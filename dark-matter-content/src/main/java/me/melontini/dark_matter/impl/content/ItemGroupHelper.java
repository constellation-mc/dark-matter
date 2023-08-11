package me.melontini.dark_matter.content;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Experimental
public class ItemGroupHelper {
    private ItemGroupHelper() {
        throw new UnsupportedOperationException();
    }
    public static final Map<ItemGroup, List<InjectEntries>> INJECTED_GROUPS = new ConcurrentHashMap<>();

    public static void addItemGroupInjection(ItemGroup group, InjectEntries injectEntries) {
        if (FabricLoader.getInstance().isModLoaded("fabric-item-group-api-v1")) {
            ItemGroupEvents.modifyEntriesEvent(group).register(entries -> injectEntries.inject(entries.getEnabledFeatures(), entries.shouldShowOpRestrictedItems(), entries));
            return;
        }
        var list = INJECTED_GROUPS.computeIfAbsent(group, group1 -> new ArrayList<>());

        if (!list.contains(injectEntries)) {
            list.add(injectEntries);
        }
    }

    @FunctionalInterface
    public interface InjectEntries {
        void inject(FeatureSet enabledFeatures, boolean operatorEnabled, ItemGroup.Entries entriesImpl);
    }
}