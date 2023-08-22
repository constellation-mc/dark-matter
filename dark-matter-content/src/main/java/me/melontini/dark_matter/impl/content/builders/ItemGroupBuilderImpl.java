package me.melontini.dark_matter.impl.content.builders;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.content.ContentBuilder;
import me.melontini.dark_matter.api.content.interfaces.AnimatedItemGroup;
import me.melontini.dark_matter.api.content.interfaces.DarkMatterEntries;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import me.melontini.dark_matter.impl.content.DarkMatterEntriesImpl;
import me.melontini.dark_matter.impl.content.interfaces.ItemGroupArrayExtender;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.function.Supplier;

public class ItemGroupBuilderImpl implements ContentBuilder.ItemGroupBuilder {

    private final Identifier identifier;
    private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
    private Supplier<AnimatedItemGroup> animatedIcon;
    private String texture;
    private DarkMatterEntries.Collector entries;
    private Text displayName;

    public ItemGroupBuilderImpl(Identifier id) {
        if (!FabricLoader.getInstance().isModLoaded("fabric-item-groups-v0")) DarkMatterLog.warn("Building {} ItemGroup without Fabric Item Groups", id);
        this.identifier = id;
    }

    public ContentBuilder.ItemGroupBuilder icon(Supplier<ItemStack> itemStackSupplier) {
        MakeSure.notNull(itemStackSupplier, "couldn't build: " + identifier);
        this.icon = itemStackSupplier;
        return this;
    }

    public ContentBuilder.ItemGroupBuilder animatedIcon(Supplier<AnimatedItemGroup> animatedIcon) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return this;
        MakeSure.notNull(animatedIcon, "couldn't build: " + identifier);
        this.animatedIcon = animatedIcon;
        return this;
    }

    public ContentBuilder.ItemGroupBuilder texture(String texture) {
        MakeSure.notEmpty(texture, "couldn't build: " + identifier);
        this.texture = texture;
        return this;
    }

    public ContentBuilder.ItemGroupBuilder entries(DarkMatterEntries.Collector collector) {
        MakeSure.notNull(collector, "couldn't build: " + identifier);
        this.entries = collector;
        return this;
    }

    public ContentBuilder.ItemGroupBuilder displayName(Text displayName) {
        MakeSure.notNull(displayName, "couldn't build: " + identifier);
        this.displayName = displayName;
        return this;
    }


    public ItemGroup build() {
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

        if (this.animatedIcon != null) itemGroup.dm$setIconAnimation(this.animatedIcon);
        if (this.texture != null) itemGroup.setTexture(this.texture);

        return itemGroup;
    }

}
