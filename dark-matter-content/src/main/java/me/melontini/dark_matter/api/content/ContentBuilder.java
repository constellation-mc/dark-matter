package me.melontini.dark_matter.api.content;

import com.mojang.datafixers.types.Type;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.content.interfaces.AnimatedItemGroup;
import me.melontini.dark_matter.api.minecraft.util.TextUtil;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import me.melontini.dark_matter.impl.content.RegistryInternals;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Most things don't work without Fabric API.
 */
@SuppressWarnings("unused")
public class ContentBuilder {
    private ContentBuilder() {
        throw new UnsupportedOperationException();
    }
    public static class ItemBuilder<T extends Item> {
        private final Identifier identifier;
        private final Supplier<T> itemSupplier;
        private BooleanSupplier register = () -> true;
        private ItemGroup itemGroup;

        private ItemBuilder(Identifier identifier, Supplier<T> itemSupplier) {
            this.identifier = identifier;
            this.itemSupplier = itemSupplier;
        }

        public static <T extends Item> ItemBuilder<T> create(Identifier identifier, Supplier<T> itemSupplier) {
            MakeSure.notNull(identifier, "null identifier provided.");

            return new ItemBuilder<>(identifier, itemSupplier);
        }

        public ItemBuilder<T> registerCondition(BooleanSupplier booleanSupplier) {
            MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
            this.register = booleanSupplier;
            return this;
        }

        public ItemBuilder<T> registerCondition(boolean bool) {
            this.register = () -> bool;
            return this;
        }

        public ItemBuilder<T> itemGroup(ItemGroup group) {
            this.itemGroup = group;
            return this;
        }

        public T build() {
            if (this.register.getAsBoolean()) {
                T item = this.itemSupplier.get();

                Registry.register(Registries.ITEM, this.identifier, item);
                if (this.itemGroup != null) ItemGroupHelper.addItemGroupInjection(this.itemGroup, (enabledFeatures, operatorEnabled, entriesImpl) -> entriesImpl.add(item));
                return item;
            }
            return null;
        }
    }

    public static class BlockBuilder<T extends Block> {
        private final Identifier identifier;
        private final Supplier<T> blockSupplier;
        private BooleanSupplier register = () -> true;
        private ItemFactory<?> itemFactory;
        private BlockEntityFactory<?> blockEntityFactory;

        private BlockBuilder(Identifier identifier, Supplier<T> blockSupplier) {
            this.identifier = identifier;
            this.blockSupplier = blockSupplier;
        }

        public static <T extends Block> BlockBuilder<T> create(Identifier identifier, Supplier<T> blockSupplier) {
            MakeSure.notNull(identifier, "null identifier provided.");

            return new BlockBuilder<>(identifier, blockSupplier);
        }

        public BlockBuilder<T> registerCondition(BooleanSupplier booleanSupplier) {
            MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
            this.register = booleanSupplier;
            return this;
        }

        public BlockBuilder<T> registerCondition(boolean bool) {
            this.register = () -> bool;
            return this;
        }

        public <I extends Item> BlockBuilder<T> item(ItemFactory<I> factory) {
            MakeSure.notNull(factory, "couldn't build: " + identifier);
            this.itemFactory = factory;
            return this;
        }

        public <B extends BlockEntity> BlockBuilder<T> blockEntity(BlockEntityFactory<B> factory) {
            MakeSure.notNull(factory, "couldn't build: " + identifier);
            this.blockEntityFactory = factory;
            return this;
        }

        public T build() {
            if (this.register.getAsBoolean()) {
                T block = this.blockSupplier.get();

                if (itemFactory != null) itemFactory.produce(block, this.identifier).build();
                if (blockEntityFactory != null) blockEntityFactory.produce(block, this.identifier).build();

                Registry.register(Registries.BLOCK, this.identifier, block);
                return block;
            }
            return null;
        }

        @FunctionalInterface
        public interface ItemFactory<B extends Item> {
            ItemBuilder<B> produce(Block block, Identifier identifier);
        }

        @FunctionalInterface
        public interface BlockEntityFactory<I extends BlockEntity> {
            BlockEntityBuilder<I> produce(Block block, Identifier identifier);
        }
    }

    public static class BlockEntityBuilder<T extends BlockEntity> {
        private final BlockEntityType.BlockEntityFactory<? extends T> factory;
        private final Set<Block> blocks;
        private final Identifier identifier;
        private BooleanSupplier register = () -> true;

        private BlockEntityBuilder(Identifier id, BlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks) {
            this.identifier = id;
            this.factory = factory;
            this.blocks = new HashSet<>(blocks.length);
            Collections.addAll(this.blocks, blocks);
        }

        public static <T extends BlockEntity> BlockEntityBuilder<T> create(Identifier id, BlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks) {
            MakeSure.notNull(id, "null identifier provided. Possible method caller: " + Utilities.getCallerName());
            MakeSure.notNull(factory, "couldn't build: " + id);
            MakeSure.notEmpty(blocks, "At least 1 block required. couldn't build: " + id);

            return new BlockEntityBuilder<>(id, factory, blocks);
        }

        public BlockEntityBuilder<T> registerCondition(BooleanSupplier booleanSupplier) {
            MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
            this.register = booleanSupplier;
            return this;
        }

        public BlockEntityBuilder<T> registerCondition(boolean bool) {
            this.register = () -> bool;
            return this;
        }

        @Override
        public String toString() {
            return "BlockEntityBuilder{" +
                    "identifier=" + identifier +
                    '}';
        }

        public BlockEntityType<T> build() {
            return build(null);
        }

        public BlockEntityType<T> build(Type<?> type) {
            if (this.register.getAsBoolean()) {
                BlockEntityType<T> t = BlockEntityType.Builder.<T>create(factory, blocks.toArray(Block[]::new)).build(type);
                Registry.register(Registries.BLOCK_ENTITY_TYPE, identifier, t);
                for (Block block : blocks) {
                    RegistryInternals.putBlockIfAbsent(block, t);
                }
                return t;
            }
            return null;
        }
    }

    public static class ItemGroupBuilder {
        private final Identifier identifier;
        private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
        private AnimatedItemGroup animatedIcon;
        private String texture;
        private Consumer<Collection<ItemStack>> tabStacks;
        private Text displayName;
        private Consumer<Collection<ItemStack>> searchTabStacks;

        private ItemGroupBuilder(Identifier id) {
            if (!FabricLoader.getInstance().isModLoaded("fabric-item-group-api-v1")) DarkMatterLog.warn("Building {} ItemGroup without Fabric API", id);
            this.identifier = id;
        }

        public static ItemGroupBuilder create(Identifier id) {
            return new ItemGroupBuilder(id);
        }

        public ItemGroupBuilder icon(ItemStack itemStack) {
            MakeSure.notNull(itemStack, "couldn't build: " + identifier);
            this.icon = () -> itemStack;
            return this;
        }

        public ItemGroupBuilder icon(Supplier<ItemStack> itemStackSupplier) {
            MakeSure.notNull(itemStackSupplier, "couldn't build: " + identifier);
            this.icon = itemStackSupplier;
            return this;
        }

        public ItemGroupBuilder animatedIcon(Supplier<AnimatedItemGroup> animatedIcon) {
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return this;
            MakeSure.notNull(animatedIcon, "couldn't build: " + identifier);
            this.animatedIcon = animatedIcon.get();
            return this;
        }

        public ItemGroupBuilder texture(String texture) {
            MakeSure.notEmpty(texture, "couldn't build: " + identifier);
            this.texture = texture;
            return this;
        }

        public ItemGroupBuilder entries(Consumer<Collection<ItemStack>> parentTabStacks) {
            MakeSure.notNull(parentTabStacks, "couldn't build: " + identifier);
            this.tabStacks = parentTabStacks;
            return this;
        }

        public ItemGroupBuilder entries(Consumer<Collection<ItemStack>> parentTabStacks, Consumer<Collection<ItemStack>> searchTabStacks) {
            MakeSure.notNull(parentTabStacks, "couldn't build: " + identifier);
            this.tabStacks = parentTabStacks;
            this.searchTabStacks = searchTabStacks;
            return this;
        }

        public ItemGroupBuilder displayName(Text displayName) {
            MakeSure.notNull(displayName, "couldn't build: " + identifier);
            this.displayName = displayName;
            return this;
        }

        public ItemGroup build() {
            ItemGroup.Builder builder;
            if (FabricLoader.getInstance().isModLoaded("fabric-item-group-api-v1")) {
                builder = FabricItemGroup.builder(this.identifier);
            } else {
                builder = new ItemGroup.Builder(null, -1);
            }
            builder.entries((enabledFeatures, entries, operatorEnabled) -> {
                if (FabricLoader.getInstance().isModLoaded("fabric-item-group-api-v1") && entries instanceof FabricItemGroupEntries fabricEntries) {
                    if (this.tabStacks != null) this.tabStacks.accept(fabricEntries.getDisplayStacks());
                    if (this.searchTabStacks != null) this.searchTabStacks.accept(fabricEntries.getSearchTabStacks());
                } else if (entries instanceof ItemGroup.EntriesImpl) {
                    if (this.tabStacks != null) this.tabStacks.accept(((ItemGroup.EntriesImpl) entries).parentTabStacks = new LinkedList<>());
                    if (this.searchTabStacks != null) this.searchTabStacks.accept(((ItemGroup.EntriesImpl) entries).searchTabStacks = new LinkedHashSet<>());
                }
            });
            builder.icon(() -> ItemGroupBuilder.this.icon.get());

            builder.displayName(Objects.requireNonNullElseGet(this.displayName, () -> TextUtil.translatable("itemGroup." + this.identifier.toString().replace(":", "."))));
            if (this.texture != null) builder.texture(this.texture);

            ItemGroup group = builder.build();
            if (this.animatedIcon != null) group.dm$setIconAnimation(this.animatedIcon);
            return group;
        }
    }
}