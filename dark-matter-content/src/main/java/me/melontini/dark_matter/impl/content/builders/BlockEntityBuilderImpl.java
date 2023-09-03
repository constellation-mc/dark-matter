package me.melontini.dark_matter.impl.content.builders;

import com.mojang.datafixers.types.Type;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.content.ContentBuilder;
import me.melontini.dark_matter.impl.content.RegistryInternals;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class BlockEntityBuilderImpl<T extends BlockEntity> implements ContentBuilder.BlockEntityBuilder<T> {

    private final BlockEntityType.BlockEntityFactory<? extends T> factory;
    private final Set<Block> blocks;
    private Type<?> type = null;
    private final Identifier identifier;
    private BooleanSupplier register = () -> true;

    public BlockEntityBuilderImpl(Identifier id, BlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks) {
        MakeSure.notNull(id, "null identifier provided.");
        MakeSure.notNull(factory, "couldn't build: " + id);
        MakeSure.notEmpty(blocks, "At least 1 block required. couldn't build: " + id);

        this.identifier = id;
        this.factory = factory;
        this.blocks = new HashSet<>(blocks.length);
        Collections.addAll(this.blocks, blocks);
    }

    @Override
    public ContentBuilder.BlockEntityBuilder<T> type(Type<?> type) {
        MakeSure.notNull(type, "couldn't build: " + identifier);
        this.type = type;
        return this;
    }

    public ContentBuilder.BlockEntityBuilder<T> registerCondition(BooleanSupplier booleanSupplier) {
        MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
        this.register = booleanSupplier;
        return this;
    }

    @Override
    public @Nullable BlockEntityType<T> build() {
        if (this.register.getAsBoolean()) {
            BlockEntityType<T> t = BlockEntityType.Builder.<T>create(factory, blocks.toArray(Block[]::new)).build(type);
            Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, t);
            for (Block block : blocks) {
                RegistryInternals.putBlockIfAbsent(block, t);
            }
            return t;
        }
        return null;
    }
}
