package me.melontini.dark_matter.api.content;

import me.melontini.dark_matter.impl.content.RegistryInternals;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Doesn't work without Fabric API
 */
public class RegistryUtil {

    private RegistryUtil() {
        throw new UnsupportedOperationException();
    }

    public static <T extends BlockEntity> @Nullable BlockEntityType<T> asBlockEntity(@NotNull Block block) {
        return getBlockEntityFromBlock(block);
    }

    public static <T extends BlockEntity> @Nullable BlockEntityType<T> getBlockEntityFromBlock(@NotNull Block block) {
        return RegistryInternals.getBlockEntityFromBlock(block);
    }

    public static <T extends Item> T asItem(Block block) {
        return block != null ? (T) block.asItem() : null;
    }

    public static @Nullable <T extends Item> T createItem(Identifier id, Supplier<T> supplier) {
        return createItem(true, id, supplier);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Item> T createItem(boolean shouldRegister, Identifier id, Supplier<T> supplier) {
        return RegistryInternals.createItem(shouldRegister, id, supplier);
    }

    public static <T extends Entity> EntityType<T> createEntityType(Identifier id, EntityType.Builder<T> builder) {
        return createEntityType(true, id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Entity> EntityType<T> createEntityType(boolean shouldRegister, Identifier id, EntityType.Builder<T> builder) {
        return RegistryInternals.createEntityType(shouldRegister, id, builder);
    }

    public static <T extends Entity> EntityType<T> createEntityType(Identifier id, FabricEntityTypeBuilder<T> builder) {
        return createEntityType(true, id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Entity> EntityType<T> createEntityType(boolean shouldRegister, Identifier id, FabricEntityTypeBuilder<T> builder) {
        return RegistryInternals.createEntityType(shouldRegister, id, builder);
    }

    public static @Nullable <T extends Block> T createBlock(Identifier id, Supplier<T> supplier) {
        return createBlock(true, id, supplier);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Block> T createBlock(boolean shouldRegister, Identifier id, Supplier<T> supplier) {
        return RegistryInternals.createBlock(shouldRegister, id, supplier);
    }

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(Identifier id, BlockEntityType.Builder<T> builder) {
        return createBlockEntity(true, id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends BlockEntity> BlockEntityType<T> createBlockEntity(boolean shouldRegister, Identifier id, BlockEntityType.Builder<T> builder) {
        return RegistryInternals.createBlockEntity(shouldRegister, id, builder);
    }

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(Identifier id, FabricBlockEntityTypeBuilder<T> builder) {
        return createBlockEntity(true, id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends BlockEntity> BlockEntityType<T> createBlockEntity(boolean shouldRegister, Identifier id, FabricBlockEntityTypeBuilder<T> builder) {
        return RegistryInternals.createBlockEntity(shouldRegister, id, builder);
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandler(Identifier id, Supplier<ScreenHandlerType.Factory<T>> factory) {
        return createScreenHandler(true, id, factory);
    }

    @Contract("false, _, _ -> null")
    public static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandler(boolean shouldRegister, Identifier id, Supplier<ScreenHandlerType.Factory<T>> factory) {
        return RegistryInternals.createScreenHandler(shouldRegister, id, factory);
    }

}
