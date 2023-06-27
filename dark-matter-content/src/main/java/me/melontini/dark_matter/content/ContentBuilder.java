package me.melontini.dark_matter.content;

import com.mojang.datafixers.types.Type;
import me.melontini.dark_matter.DarkMatterLog;
import me.melontini.dark_matter.content.interfaces.AnimatedItemGroup;
import me.melontini.dark_matter.util.MakeSure;
import me.melontini.dark_matter.util.Utilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.*;

/**
 * Most things don't work without Fabric API.
 */
@SuppressWarnings("unused")
public class ContentBuilder {
    private ContentBuilder() {
        throw new UnsupportedOperationException();
    }
    public static class ItemBuilder<T extends Item> {
        private final Class<T> itemClass;
        private final Identifier identifier;
        private final Item.Settings settings;
        private ItemGroup itemGroup;
        private final Object[] params;
        private BooleanSupplier shouldLoad = () -> true;

        private ItemBuilder(Class<T> itemClass, Identifier id, Object... params) {
            this.itemClass = itemClass;
            this.identifier = id;
            this.params = params;

            Item.Settings temp = null;
            for (Object param : params) {
                if (param instanceof Item.Settings settings1) {
                    temp = settings1;//lazy
                }
            }
            this.settings = temp;
        }

        public static <T extends Item> ItemBuilder<T> create(Class<T> itemClass, Identifier id, Object... params) {
            MakeSure.notNull(id, "null identifier provided. Possible method caller: " + Utilities.getCallerName());
            MakeSure.notNull(itemClass, "couldn't build: " + id);

            return new ItemBuilder<>(itemClass, id, params);
        }

        public ItemBuilder<T> loadCondition(BooleanSupplier booleanSupplier) {
            MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
            this.shouldLoad = booleanSupplier;
            return this;
        }

        public ItemBuilder<T> loadCondition(boolean bool) {
            this.shouldLoad = () -> bool;
            return this;
        }

        public ItemBuilder<T> itemGroup(ItemGroup group) {
            return group(group);
        }

        public ItemBuilder<T> group(ItemGroup group) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", Item.Settings not found");
            MakeSure.notNull(group, "couldn't build: " + identifier);
            this.itemGroup = group;
            return this;
        }

        public ItemBuilder<T> food(FoodComponent foodComponent) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", Item.Settings not found");
            MakeSure.notNull(foodComponent, "couldn't build: " + identifier);
            this.settings.food(foodComponent);
            return this;
        }

        public ItemBuilder<T> maxCount(int maxCount) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", Item.Settings not found");
            this.settings.maxCount(maxCount);
            return this;
        }

        public ItemBuilder<T> maxDamageIfAbsent(int maxDamage) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", Item.Settings not found");
            this.settings.maxDamageIfAbsent(maxDamage);
            return this;
        }

        public ItemBuilder<T> maxDamage(int maxDamage) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", Item.Settings not found");
            this.settings.maxDamage(maxDamage);
            return this;
        }

        public ItemBuilder<T> recipeRemainder(Item recipeRemainder) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", Item.Settings not found");
            MakeSure.notNull(recipeRemainder, "couldn't build: " + identifier);
            this.settings.recipeRemainder(recipeRemainder);
            return this;
        }

        public ItemBuilder<T> rarity(Rarity rarity) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", Item.Settings not found");
            MakeSure.notNull(rarity, "couldn't build: " + identifier);
            this.settings.rarity(rarity);
            return this;
        }

        public ItemBuilder<T> fireproof() {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", Item.Settings not found");
            this.settings.fireproof();
            return this;
        }

        @Override
        public String toString() {
            return "ItemBuilder{" +
                    "itemClass=" + itemClass.getSimpleName() +
                    ", identifier=" + identifier +
                    '}';
        }

        public T build() {
            if (shouldLoad.getAsBoolean()) {
                return RegistryUtil.createItem(true, itemClass, identifier, Optional.ofNullable(itemGroup), params);
            }
            return null;
        }
    }

    public static class BlockBuilder<T extends Block> {
        private final Class<T> blockClass;
        private final Identifier identifier;
        private final AbstractBlock.Settings settings;
        private final Object[] params;
        private BooleanSupplier shouldLoad = () -> true;
        private BuilderFactory builder;
        private BlockEntityFactory blockEntityBuilder;

        private BlockBuilder(Class<T> blockClass, Identifier id, Object... params) {
            this.blockClass = blockClass;
            this.identifier = id;
            this.params = params;

            AbstractBlock.Settings temp = null;
            for (Object param : params) {
                if (param instanceof AbstractBlock.Settings settings1) {
                    temp = settings1;//lazy
                }
            }
            this.settings = temp;
        }

        public static <T extends Block> BlockBuilder<T> create(Class<T> blockClass, Identifier id, Object... params) {
            MakeSure.notNull(id, "null identifier provided. Possible method caller: " + Utilities.getCallerName());
            MakeSure.notNull(blockClass, "couldn't build: " + id);

            return new BlockBuilder<>(blockClass, id, params);
        }

        public BlockBuilder<T> loadCondition(BooleanSupplier booleanSupplier) {
            MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
            this.shouldLoad = booleanSupplier;
            return this;
        }

        public BlockBuilder<T> loadCondition(boolean bool) {
            this.shouldLoad = () -> bool;
            return this;
        }

        public BlockBuilder<T> noCollision() {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.noCollision();
            return this;
        }

        public BlockBuilder<T> nonOpaque() {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.nonOpaque();
            return this;
        }

        public BlockBuilder<T> slipperiness(float slipperiness) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.slipperiness(slipperiness);
            return this;
        }

        public BlockBuilder<T> velocityMultiplier(float velocityMultiplier) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.velocityMultiplier(velocityMultiplier);
            return this;
        }

        public BlockBuilder<T> jumpVelocityMultiplier(float jumpVelocityMultiplier) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.jumpVelocityMultiplier(jumpVelocityMultiplier);
            return this;
        }

        public BlockBuilder<T> sounds(BlockSoundGroup soundGroup) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.sounds(soundGroup);
            return this;
        }

        public BlockBuilder<T> luminance(ToIntFunction<BlockState> luminance) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.luminance(luminance);
            return this;
        }

        public BlockBuilder<T> strength(float hardness, float resistance) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.strength(hardness, resistance);
            return this;
        }

        public BlockBuilder<T> breakInstantly() {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.breakInstantly();
            return this;
        }

        public BlockBuilder<T> strength(float strength) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.strength(strength, strength);
            return this;
        }

        public BlockBuilder<T> ticksRandomly() {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.ticksRandomly();
            return this;
        }

        public BlockBuilder<T> dynamicBounds() {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.dynamicBounds();
            return this;
        }

        public BlockBuilder<T> dropsNothing() {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.dropsNothing();
            return this;
        }

        public BlockBuilder<T> dropsLike(Block source) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.dropsLike(source);
            return this;
        }

        public BlockBuilder<T> air() {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.air();
            return this;
        }

        public BlockBuilder<T> allowsSpawning(AbstractBlock.TypedContextPredicate<EntityType<?>> predicate) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.allowsSpawning(predicate);
            return this;
        }

        public BlockBuilder<T> solidBlock(AbstractBlock.ContextPredicate predicate) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.solidBlock(predicate);
            return this;
        }

        public BlockBuilder<T> suffocates(AbstractBlock.ContextPredicate predicate) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.suffocates(predicate);
            return this;
        }

        public BlockBuilder<T> blockVision(AbstractBlock.ContextPredicate predicate) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.blockVision(predicate);
            return this;
        }

        public BlockBuilder<T> postProcess(AbstractBlock.ContextPredicate predicate) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.postProcess(predicate);
            return this;
        }

        public BlockBuilder<T> emissiveLighting(AbstractBlock.ContextPredicate predicate) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.emissiveLighting(predicate);
            return this;
        }

        public BlockBuilder<T> requiresTool() {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.requiresTool();
            return this;
        }

        public BlockBuilder<T> mapColor(MapColor color) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.mapColor(color);
            return this;
        }

        public BlockBuilder<T> hardness(float hardness) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.hardness(hardness);
            return this;
        }

        public BlockBuilder<T> resistance(float resistance) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.resistance(resistance);
            return this;
        }

        public BlockBuilder<T> offsetType(AbstractBlock.OffsetType offsetType) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.offsetType(offsetType);
            return this;
        }

        public BlockBuilder<T> offsetType(Function<BlockState, AbstractBlock.OffsetType> offsetType) {
            MakeSure.notNull(settings, "couldn't build: " + identifier + ", AbstractBlock.Settings not found");
            this.settings.offsetType(offsetType);
            return this;
        }

        public BlockBuilder<T> itemBuilder(BuilderFactory builder) {
            return this.item(builder);
        }

        public BlockBuilder<T> item(BuilderFactory builder) {
            MakeSure.notNull(builder, "couldn't build: " + identifier);
            this.builder = builder;
            return this;
        }

        public BlockBuilder<T> blockEntityBuilder(BlockEntityFactory builder) {
            return blockEntity(builder);
        }

        public BlockBuilder<T> blockEntity(BlockEntityFactory builder) {
            MakeSure.notNull(builder, "couldn't build: " + identifier);
            this.blockEntityBuilder = builder;
            return this;
        }

        @Override
        public String toString() {
            return "BlockBuilder{" +
                    "blockClass=" + blockClass.getSimpleName() +
                    ", identifier=" + identifier +
                    '}';
        }

        public T build() {
            if (shouldLoad.getAsBoolean()) {
                T block = RegistryUtil.createBlock(true, blockClass, identifier, params);
                if (builder != null) builder.factory(block, identifier).build();
                if (blockEntityBuilder != null) blockEntityBuilder.factory(block, identifier).build();
                return block;
            }
            return null;
        }

        @FunctionalInterface
        public interface BuilderFactory {
            ItemBuilder<? extends Item> factory(Block block, Identifier id);
        }

        @FunctionalInterface
        public interface BlockEntityFactory {
            BlockEntityBuilder<? extends BlockEntity> factory(Block block, Identifier id);
        }
    }

    public static class BlockEntityBuilder<T extends BlockEntity> {
        private final BlockEntityType.BlockEntityFactory<? extends T> factory;
        private final Set<Block> blocks;
        private final Identifier identifier;
        private BooleanSupplier shouldLoad = () -> true;

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

        public BlockEntityBuilder<T> loadCondition(BooleanSupplier booleanSupplier) {
            MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
            this.shouldLoad = booleanSupplier;
            return this;
        }

        public BlockEntityBuilder<T> loadCondition(boolean bool) {
            this.shouldLoad = () -> bool;
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
            if (shouldLoad.getAsBoolean()) {
                BlockEntityType<T> t = BlockEntityType.Builder.<T>create(factory, blocks.toArray(Block[]::new)).build(type);
                Registry.register(Registries.BLOCK_ENTITY_TYPE, identifier, t);
                for (Block block : blocks) {
                    RegistryUtil.BLOCK_ENTITY_LOOKUP.putIfAbsent(block, t);
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

        public ItemGroupBuilder entries(Consumer<Collection<ItemStack>> parentTabStacks, /*99% of the time, this will be a set*/ Consumer<Collection<ItemStack>> searchTabStacks) {
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
            try {
                Class<?> clazz = Class.forName("net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup");
                builder = (ItemGroup.Builder) clazz.getMethod("builder", Identifier.class).invoke(null, identifier);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                try {
                    builder = new ItemGroup.Builder(null, -1);
                } catch (Exception e1) {
                    throw new RuntimeException("couldn't build: " + identifier, e1);
                }
            }
            builder.entries((enabledFeatures, entries, operatorEnabled) -> {
                var list = ((ItemGroup.EntriesImpl) entries).parentTabStacks = new ArrayList<>();
                var set = ((ItemGroup.EntriesImpl) entries).searchTabStacks = new LinkedHashSet<>();

                if (this.tabStacks != null) this.tabStacks.accept(list);
                if (this.searchTabStacks != null) this.searchTabStacks.accept(set);
            });
            builder.icon(() -> ItemGroupBuilder.this.icon.get());

            if (this.displayName != null) builder.displayName(this.displayName);
            if (this.texture != null) builder.texture(this.texture);

            ItemGroup group = builder.build();
            if (this.animatedIcon != null) group.setIconAnimation(this.animatedIcon);
            return group;
        }
    }
}