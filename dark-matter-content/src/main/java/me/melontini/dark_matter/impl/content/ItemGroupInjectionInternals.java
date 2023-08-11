package me.melontini.dark_matter.impl.content;

import me.melontini.dark_matter.api.content.ItemGroupHelper;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class ItemGroupInjectionInternals {
    private ItemGroupInjectionInternals() {
        throw new UnsupportedOperationException();
    }
    public static final Map<ItemGroup, List<ItemGroupHelper.InjectEntries>> INJECTED_GROUPS = new ConcurrentHashMap<>();

    public static void addItemGroupInjection(ItemGroup group, ItemGroupHelper.InjectEntries injectEntries) {
        if (FabricLoader.getInstance().isModLoaded("fabric-item-group-api-v1")) {
            ItemGroupEvents.modifyEntriesEvent(group).register(entries -> injectEntries.inject(entries.getEnabledFeatures(), entries.shouldShowOpRestrictedItems(), entries));
            return;
        }
        var list = INJECTED_GROUPS.computeIfAbsent(group, group1 -> new ArrayList<>());

        if (!list.contains(injectEntries)) {
            list.add(injectEntries);
        }
    }

}