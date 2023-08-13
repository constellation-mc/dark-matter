package me.melontini.dark_matter.impl.content;

import me.melontini.dark_matter.api.content.interfaces.DarkMatterEntries;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.*;

public class DarkMatterEntriesImpl implements DarkMatterEntries {

    private final ItemGroup.Entries entries;

    public DarkMatterEntriesImpl(ItemGroup.Entries entries) {
        this.entries = entries;

        if (entries instanceof ItemGroup.EntriesImpl) {
            ((ItemGroup.EntriesImpl) entries).parentTabStacks = new LinkedList<>();
            ((ItemGroup.EntriesImpl) entries).searchTabStacks = new LinkedHashSet<>();
        }
    }

    public void add(ItemStack stack, Visibility visibility) {
        if (entries instanceof ItemGroup.EntriesImpl impl) {
            switch (visibility) {
                case TAB_AND_SEARCH -> {
                    impl.parentTabStacks.add(stack);
                    impl.searchTabStacks.add(stack);
                }
                case TAB -> impl.parentTabStacks.add(stack);
                case SEARCH -> impl.searchTabStacks.add(stack);
            }
            return;
        }
        //Let's hope that whatever this is doesn't have dumb restrictions.
        switch (visibility) {
            case TAB_AND_SEARCH -> this.entries.add(stack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
            case TAB -> this.entries.add(stack, ItemGroup.StackVisibility.PARENT_TAB_ONLY);
            case SEARCH -> this.entries.add(stack, ItemGroup.StackVisibility.SEARCH_TAB_ONLY);
        }
    }

}
