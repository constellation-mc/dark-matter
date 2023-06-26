package me.melontini.dark_matter.enums.interfaces.internal;

import net.minecraft.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@ApiStatus.Internal
public interface EnchantmentTargetHack {
    default void dark_matter$setPredicate(Predicate<Item> predicate) {
        throw new IllegalStateException("Interface not implemented");
    }
}
