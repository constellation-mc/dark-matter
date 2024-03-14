package me.melontini.dark_matter.impl.content;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.Utilities;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

@UtilityClass
@ApiStatus.Internal
public class RegistryInternals {

    private static final Map<Block, BlockEntityType<?>> BLOCK_ENTITY_LOOKUP = Utilities.consume(new HashMap<>(), map -> {
        Registries.BLOCK_ENTITY_TYPE.forEach(beType -> {
            for (Block block : beType.blocks) {
                map.putIfAbsent(block, beType);
            }
        });
    });

    public static void putBlockIfAbsent(Block block, BlockEntityType<?> t) {
        BLOCK_ENTITY_LOOKUP.putIfAbsent(block, t);
    }

    public static <T extends BlockEntity> @Nullable BlockEntityType<T> getBlockEntityFromBlock(@Nullable Block block) {
        if (block == null) return null;

        if (BLOCK_ENTITY_LOOKUP.containsKey(block)) {
            return cast(BLOCK_ENTITY_LOOKUP.get(block));
        }

        Registries.BLOCK_ENTITY_TYPE.forEach(beType -> {
            for (Block block1 : beType.blocks) {
                BLOCK_ENTITY_LOOKUP.putIfAbsent(block1, beType);
            }
        });
        return cast(BLOCK_ENTITY_LOOKUP.get(block));
    }
}
