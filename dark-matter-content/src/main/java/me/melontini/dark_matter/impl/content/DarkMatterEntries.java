package me.melontini.dark_matter.impl.content;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DarkMatterEntries {

    private final Collection<ItemStack> tabStacks;
    private final Set<ItemStack> searchStacks;

    public DarkMatterEntries(Collection<ItemStack> tabStacks, Collection<ItemStack> searchStacks) {
        this.tabStacks = tabStacks;
        this.searchStacks = new HashSet<>(searchStacks);
    }

    public DarkMatterEntries(Collection<ItemStack> tabStacks) {
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

    public void add(ItemStack stack) {
        this.add(stack, Visibility.TAB_AND_SEARCH);
    }

    public void add(ItemConvertible item, Visibility visibility) {
        this.add(new ItemStack(item), visibility);
    }

    public void add(ItemConvertible item) {
        this.add(new ItemStack(item), Visibility.TAB_AND_SEARCH);
    }

    public void addAll(Collection<ItemStack> stacks, Visibility visibility) {
        stacks.forEach(stack -> this.add(stack, visibility));
    }

    public void addAll(Collection<ItemStack> stacks) {
        this.addAll(stacks, Visibility.TAB_AND_SEARCH);
    }

    public Collection<ItemStack> getTabStacks() {
        return tabStacks;
    }

    public Set<ItemStack> getSearchStacks() {
        return searchStacks;
    }

    public enum Visibility {
        TAB_AND_SEARCH,
        TAB,
        SEARCH;
    }

    public interface Collector {
        void collect(DarkMatterEntries entries);
    }
}
