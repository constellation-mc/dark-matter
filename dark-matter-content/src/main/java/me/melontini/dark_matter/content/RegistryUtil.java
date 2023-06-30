package me.melontini.dark_matter.content;

import me.melontini.dark_matter.util.Utilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Doesn't work without Fabric API
 */
@SuppressWarnings("unused")
public class RegistryUtil {
    private RegistryUtil() {
        throw new UnsupportedOperationException();
    }
    private static boolean DONE;
    protected static final Map<Block, BlockEntityType<?>> BLOCK_ENTITY_LOOKUP = Utilities.consume(new HashMap<>(), map -> {
        Registry.BLOCK_ENTITY_TYPE.forEach(beType -> {
            for (Block block : beType.blocks) {
                map.putIfAbsent(block, beType);
            }
        });
    });

    public static <T extends BlockEntity> @Nullable BlockEntityType<T> asBlockEntity(@NotNull Block block) {
        return getBlockEntityFromBlock(block);
    }

    public static <T extends BlockEntity> @Nullable BlockEntityType<T> getBlockEntityFromBlock(@NotNull Block block) {
        if (BLOCK_ENTITY_LOOKUP.containsKey(block)) return (BlockEntityType<T>) BLOCK_ENTITY_LOOKUP.get(block);
        else {
            if (((SimpleRegistry<?>) Registry.BLOCK_ENTITY_TYPE).frozen && !DONE) {
                Registry.BLOCK_ENTITY_TYPE.forEach(beType -> {
                    for (Block block1 : beType.blocks) {
                        BLOCK_ENTITY_LOOKUP.putIfAbsent(block1, beType);
                    }
                });
                DONE = true;
                if (BLOCK_ENTITY_LOOKUP.containsKey(block)) return (BlockEntityType<T>) BLOCK_ENTITY_LOOKUP.get(block);
            }
            if (block instanceof BlockWithEntity blockWithEntity) {
                var be = blockWithEntity.createBlockEntity(BlockPos.ORIGIN, block.getDefaultState());
                if (be != null) {
                    var type = (BlockEntityType<T>) be.getType();
                    BLOCK_ENTITY_LOOKUP.putIfAbsent(block, type);
                    be.markRemoved();
                    return type;
                }
            }
        }
        return null;
    }

    public static <T extends Item> T asItem(Block block) {
        return block != null ? (T) block.asItem() : null;
    }

    public static @Nullable <T extends Item> T createItem(Identifier id, Supplier<T> supplier) {
        return createItem(true, id, supplier);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Item> T createItem(boolean shouldRegister, Identifier id, Supplier<T> supplier) {
        if (shouldRegister) {
            T item = supplier.get();

            Registry.register(Registry.ITEM, id, item);
            return item;
        } else {
            return null;
        }
    }

    public static <T extends Entity> EntityType<T> createEntityType(Identifier id, EntityType.Builder<T> builder) {
        return createEntityType(true, id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Entity> EntityType<T> createEntityType(boolean shouldRegister, Identifier id, EntityType.Builder<T> builder) {
        if (shouldRegister) {
            EntityType<T> type = builder.build(Pattern.compile("[\\W]").matcher(id.toString()).replaceAll("_"));
            Registry.register(Registry.ENTITY_TYPE, id, type);
            return type;
        }
        return null;
    }

    public static @Nullable <T extends Block> T createBlock(Identifier id, Supplier<T> supplier) {
        return createBlock(true, id, supplier);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends Block> T createBlock(boolean shouldRegister, Identifier id, Supplier<T> supplier) {
        if (shouldRegister) {
            T block = supplier.get();

            Registry.register(Registry.BLOCK, id, block);
            return block;
        }
        return null;
    }

    public static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(Identifier id, BlockEntityType.Builder<T> builder) {
        return createBlockEntity(true, id, builder);
    }

    @Contract("false, _, _ -> null")
    public static @Nullable <T extends BlockEntity> BlockEntityType<T> createBlockEntity(boolean shouldRegister, Identifier id, BlockEntityType.Builder<T> builder) {
        if (shouldRegister) {
            BlockEntityType<T> type = builder.build(null);
            Registry.register(Registry.BLOCK_ENTITY_TYPE, id, type);
            return type;
        }
        return null;
    }

    public static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandler(Identifier id, Supplier<ScreenHandlerType.Factory<T>> factory) {
        return createScreenHandler(true, id, factory);
    }

    @Contract("false, _, _ -> null")
    public static <T extends ScreenHandler> ScreenHandlerType<T> createScreenHandler(boolean shouldRegister, Identifier id, Supplier<ScreenHandlerType.Factory<T>> factory) {
        if (shouldRegister) {
            Registry.register(Registry.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory.get()));
        }
        return null;
    }
}
