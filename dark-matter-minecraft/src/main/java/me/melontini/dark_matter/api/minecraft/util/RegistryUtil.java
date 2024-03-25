package me.melontini.dark_matter.api.minecraft.util;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.impl.minecraft.util.RegistryInternals;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@UtilityClass
@SuppressWarnings("unused")
public class RegistryUtil {

    @Contract("null -> null")
    public <T extends BlockEntity> BlockEntityType<T> asBlockEntity(@Nullable Block block) {
        return RegistryInternals.getBlockEntityFromBlock(block);
    }

    @Contract("null -> null")
    public <T extends Item> T asItem(@Nullable ItemConvertible convertible) {
        return convertible != null ? Utilities.cast(convertible.asItem()) : null;
    }

    public <T extends ScreenHandler> Supplier<ScreenHandlerType<T>> screenHandlerType(BiFunction<Integer, PlayerInventory, T> factory) {
        return () -> new ScreenHandlerType<>(factory::apply, FeatureSet.empty());
    }

    public <V, T extends V> @Nullable T register(Registry<V> registry, Identifier id, Supplier<T> entry) {
        return register(true, registry, id, entry);
    }

    public <V, T extends V> @Nullable T register(BooleanSupplier condition, Registry<V> registry, Identifier id, Supplier<T> entry) {
        return register(condition.getAsBoolean(), registry, id, entry);
    }

    public <V, T extends V> @Nullable T register(boolean condition, Registry<V> registry, Identifier id, Supplier<T> entry) {
        if (condition) {
            return Registry.register(registry, id, entry.get());
        }
        return null;
    }
}
