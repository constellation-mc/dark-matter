package me.melontini.dark_matter.api.content;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.impl.content.RegistryInternals;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Doesn't work without Fabric API
 */
@UtilityClass
@SuppressWarnings("unused")
public class RegistryUtil {

    public static <T extends BlockEntity> @Nullable BlockEntityType<T> asBlockEntity(@NotNull Block block) {
        return RegistryInternals.getBlockEntityFromBlock(block);
    }

    public static <T extends Item> T asItem(ItemConvertible convertible) {
        return convertible != null ? Utilities.cast(convertible.asItem()) : null;
    }

    public <V, T extends V> @Nullable T register(BooleanSupplier condition, Registry<V> registry, Identifier id, Supplier<T> entry) {
        if (condition.getAsBoolean()) {
            return Registry.register(registry, id, entry.get());
        }
        return null;
    }

    public <V, T extends V> @Nullable T register(BooleanSupplier condition, Registry<V> registry, RegistryKey<V> id, Supplier<T> entry) {
        if (condition.getAsBoolean()) {
            return Registry.register(registry, id, entry.get());
        }
        return null;
    }
}
