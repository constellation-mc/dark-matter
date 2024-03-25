package me.melontini.dark_matter.api.item_group;

import me.melontini.dark_matter.api.base.util.MathUtil;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

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

    default void appendStacks(Collection<ItemStack> list) {
        appendStacks(list, true);
    }

    default void appendStacks(Collection<ItemStack> list, boolean lineBreak) {
        if (list == null || list.isEmpty()) return; //we shouldn't add line breaks if there are no items.

        int rows = MathUtil.fastCeil(list.size() / 9d);
        this.addAll(list, DarkMatterEntries.Visibility.TAB);
        int left = (rows * 9) - list.size();
        for (int i = 0; i < left; i++) {
            this.add(ItemStack.EMPTY, DarkMatterEntries.Visibility.TAB); //fill the gaps
        }
        if (lineBreak) this.addAll(DefaultedList.ofSize(9, ItemStack.EMPTY), DarkMatterEntries.Visibility.TAB); //line break
    }

    enum Visibility {
        TAB_AND_SEARCH,
        TAB,
        SEARCH;
    }

    interface Collector {
        void collect(DarkMatterEntries entries);
    }
}
