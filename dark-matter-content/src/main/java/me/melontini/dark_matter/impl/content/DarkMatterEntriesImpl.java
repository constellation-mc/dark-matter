package me.melontini.dark_matter.impl.content;

import me.melontini.dark_matter.api.content.interfaces.DarkMatterEntries;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DarkMatterEntriesImpl implements DarkMatterEntries {

    private final Collection<ItemStack> tabStacks;
    private final Set<ItemStack> searchStacks;

    public DarkMatterEntriesImpl(Collection<ItemStack> tabStacks, Collection<ItemStack> searchStacks) {
        this.tabStacks = tabStacks;
        this.searchStacks = new HashSet<>(searchStacks);
    }

    public DarkMatterEntriesImpl(Collection<ItemStack> tabStacks) {
        this(tabStacks, new ArrayList<>());
    }

    public void add(ItemStack stack, Visibility visibility) {
        switch (visibility) {
            case TAB_AND_SEARCH -> {
                this.tabStacks.add(stack);
                this.searchStacks.add(stack);
            }
            case TAB -> this.tabStacks.add(stack);
            case SEARCH -> this.searchStacks.add(stack);
        }
    }

    public Collection<ItemStack> getTabStacks() {
        return tabStacks;
    }

    public Set<ItemStack> getSearchStacks() {
        return searchStacks;
    }

}
