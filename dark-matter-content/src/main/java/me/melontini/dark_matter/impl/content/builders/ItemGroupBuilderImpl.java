package me.melontini.dark_matter.impl.content.builders;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.content.ContentBuilder;
import me.melontini.dark_matter.api.content.interfaces.DarkMatterEntries;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import me.melontini.dark_matter.impl.content.DarkMatterEntriesImpl;
import me.melontini.dark_matter.impl.content.interfaces.ItemGroupArrayExtender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ItemGroupBuilderImpl implements ContentBuilder.ItemGroupBuilder {

    private final Identifier identifier;
    private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
    private String texture;
    private DarkMatterEntries.Collector entries;
    private BooleanSupplier register = Utilities.getTruth();
    private Text displayName;

    public ItemGroupBuilderImpl(Identifier id) {
        MakeSure.notNull(id, "null identifier provided.");

        if (!FabricLoader.getInstance().isModLoaded("fabric-item-groups-v0")) DarkMatterLog.warn("Building {} ItemGroup without Fabric Item Groups", id);
        this.identifier = id;
    }

    @Override
    public ContentBuilder.ItemGroupBuilder icon(Supplier<ItemStack> itemStackSupplier) {
        MakeSure.notNull(itemStackSupplier, "couldn't build: " + identifier);
        this.icon = itemStackSupplier;
        return this;
    }

    @Override
    public ContentBuilder.ItemGroupBuilder texture(String texture) {
        MakeSure.notEmpty(texture, "couldn't build: " + identifier);
        this.texture = texture;
        return this;
    }

    @Override
    public ContentBuilder.ItemGroupBuilder entries(DarkMatterEntries.Collector collector) {
        MakeSure.notNull(collector, "couldn't build: " + identifier);
        this.entries = collector;
        return this;
    }

    @Override
    public ContentBuilder.ItemGroupBuilder displayName(Text displayName) {
        MakeSure.notNull(displayName, "couldn't build: " + identifier);
        this.displayName = displayName;
        return this;
    }


    @Override
    public ContentBuilder.ItemGroupBuilder register(BooleanSupplier booleanSupplier) {
        MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
        this.register = booleanSupplier;
        return this;
    }

    @Override
    public Identifier getId() {
        return this.identifier;
    }

    @Override
    public ItemGroup build() {
        if (!this.register.getAsBoolean()) return null;

        ((ItemGroupArrayExtender) ItemGroup.BREWING).dark_matter$crack_array();
        ItemGroup itemGroup = new ItemGroup(ItemGroup.GROUPS.length - 1, identifier.toString().replace(":", ".")) {
            @Override
            public ItemStack getIcon() {
                return ItemGroupBuilderImpl.this.icon.get();
            }

            @Override
            public ItemStack createIcon() {
                return ItemStack.EMPTY;
            }

            @Override
            public void appendStacks(DefaultedList<ItemStack> stacks) {
                if (ItemGroupBuilderImpl.this.entries != null) {
                    DarkMatterEntriesImpl entries = new DarkMatterEntriesImpl(stacks);
                    ItemGroupBuilderImpl.this.entries.collect(entries);
                    return;
                }
                super.appendStacks(stacks);
            }

            @Override
            public Text getDisplayName() {
                if (ItemGroupBuilderImpl.this.displayName != null) return ItemGroupBuilderImpl.this.displayName;
                return super.getDisplayName();
            }
        };

        if (this.texture != null) itemGroup.setTexture(this.texture);

        return itemGroup;
    }

}
