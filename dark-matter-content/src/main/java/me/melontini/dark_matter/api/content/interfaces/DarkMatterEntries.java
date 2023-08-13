package me.melontini.dark_matter.api.content.interfaces;

import me.melontini.dark_matter.impl.content.DarkMatterEntriesImpl;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface DarkMatterEntries {
    void add(ItemStack stack, Visibility visibility);

    default void add(ItemStack stack) {
        this.add(stack, Visibility.TAB_AND_SEARCH);
    }

    default void add(ItemConvertible item, Visibility visibility) {
        this.add(new ItemStack(item), visibility);
    }

    default void add(ItemConvertible item) {
        this.add(new ItemStack(item), Visibility.TAB_AND_SEARCH);
    }

    default void addAll(Collection<ItemStack> stacks, Visibility visibility) {
        stacks.forEach(stack -> this.add(stack, visibility));
    }

    default void addAll(Collection<ItemStack> stacks) {
        this.addAll(stacks, Visibility.TAB_AND_SEARCH);
    }

    enum Visibility {
        TAB_AND_SEARCH,
        TAB,
        SEARCH;
    }

    interface Collector {
        void collect(DarkMatterEntriesImpl entries);
    }
}
