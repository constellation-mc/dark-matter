package me.melontini.dark_matter.impl.content.builders;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.content.ContentBuilder;
import me.melontini.dark_matter.api.content.RegistryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class BlockBuilderImpl<T extends Block> implements ContentBuilder.BlockBuilder<T> {

    private final Identifier identifier;
    private final Supplier<T> blockSupplier;
    private BooleanSupplier register = () -> true;
    private ContentBuilder.BlockBuilder.ItemFactory<?> itemFactory;
    private ContentBuilder.BlockBuilder.BlockEntityFactory<?> blockEntityFactory;

    public BlockBuilderImpl(Identifier identifier, Supplier<T> blockSupplier) {
        MakeSure.notNull(identifier, "null identifier provided.");
        MakeSure.notNull(blockSupplier, "couldn't build: " + identifier);

        this.identifier = identifier;
        this.blockSupplier = blockSupplier;
    }

    public ContentBuilder.BlockBuilder<T> registerCondition(BooleanSupplier booleanSupplier) {
        MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
        this.register = booleanSupplier;
        return this;
    }

    public <I extends Item> ContentBuilder.BlockBuilder<T> item(ContentBuilder.BlockBuilder.ItemFactory<I> factory) {
        MakeSure.notNull(factory, "couldn't build: " + identifier);
        this.itemFactory = factory;
        return this;
    }

    public <B extends BlockEntity> ContentBuilder.BlockBuilder<T> blockEntity(ContentBuilder.BlockBuilder.BlockEntityFactory<B> factory) {
        MakeSure.notNull(factory, "couldn't build: " + identifier);
        this.blockEntityFactory = factory;
        return this;
    }

    public T build() {
        T block = RegistryUtil.createBlock(this.register, this.identifier, this.blockSupplier);

        if (block != null) {
            if (itemFactory != null) itemFactory.produce(block, this.identifier).build();
            if (blockEntityFactory != null) blockEntityFactory.produce(block, this.identifier).build();
        }
        return block;
    }
}
