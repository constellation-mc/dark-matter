package me.melontini.dark_matter.impl.content;

import me.melontini.dark_matter.api.content.ItemGroupHelper;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
public class ItemGroupInjectionInternals {

    private ItemGroupInjectionInternals() {
        throw new UnsupportedOperationException();
    }

    private static final Map<ItemGroup, List<ItemGroupHelper.InjectEntries>> INJECTED_GROUPS = new LinkedHashMap<>();
    private static final Map<ItemGroup, List<ItemGroupHelper.InjectEntries>> VIEW = Collections.unmodifiableMap(INJECTED_GROUPS);

    public static void addItemGroupInjection(ItemGroup group, ItemGroupHelper.InjectEntries injectEntries) {
        var list = INJECTED_GROUPS.computeIfAbsent(group, group1 -> new ArrayList<>());

        if (!list.contains(injectEntries)) {
            list.add(injectEntries);
        }
    }

    public static Map<ItemGroup, List<ItemGroupHelper.InjectEntries>> getView() {
        return VIEW;
    }
}