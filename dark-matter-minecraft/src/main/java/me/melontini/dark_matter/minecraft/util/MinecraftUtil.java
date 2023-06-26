package me.melontini.dark_matter.minecraft.util;

import me.melontini.dark_matter.util.MathStuff;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.Collection;

public class MinecraftUtil {
    private MinecraftUtil() {
        throw new UnsupportedOperationException();
    }

    public static void appendStacks(Collection<ItemStack> stacks, Collection<ItemStack> list) {
        appendStacks(stacks, list, true);
    }

    public static void appendStacks(Collection<ItemStack> stacks, Collection<ItemStack> list, boolean lineBreak) {
        if (list == null || list.isEmpty()) return; //we shouldn't add line breaks if there are no items.

        int rows = MathStuff.fastCeil(list.size() / 9d);
        stacks.addAll(list);
        int left = (rows * 9) - list.size();
        for (int i = 0; i < left; i++) {
            stacks.add(ItemStack.EMPTY); //fill the gaps
        }
        if (lineBreak) stacks.addAll(DefaultedList.ofSize(9, ItemStack.EMPTY)); //line break
    }
}
