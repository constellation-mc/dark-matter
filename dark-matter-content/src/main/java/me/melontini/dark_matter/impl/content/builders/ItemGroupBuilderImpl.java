package me.melontini.dark_matter.impl.content.builders;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.content.ContentBuilder;
import me.melontini.dark_matter.api.content.ItemGroupHelper;
import me.melontini.dark_matter.api.content.interfaces.AnimatedItemGroup;
import me.melontini.dark_matter.api.content.interfaces.DarkMatterEntries;
import me.melontini.dark_matter.api.minecraft.util.TextUtil;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import me.melontini.dark_matter.impl.content.DarkMatterEntriesImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Supplier;

public class ItemGroupBuilderImpl implements ContentBuilder.ItemGroupBuilder {

    private final Identifier identifier;
    private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
    private Supplier<AnimatedItemGroup> animatedIcon;
    private String texture;
    private DarkMatterEntries.Collector entries;
    private Text displayName;

    public ItemGroupBuilderImpl(Identifier id) {
        MakeSure.notNull(id, "null identifier provided.");

        if (!FabricLoader.getInstance().isModLoaded("fabric-item-group-api-v1")) DarkMatterLog.warn("Building {} ItemGroup without Fabric Item Groups", id);
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
        ItemGroup.Builder builder;
        if (FabricLoader.getInstance().isModLoaded("fabric-item-group-api-v1")) {
            builder = FabricItemGroup.builder(this.identifier);
        } else {
            builder = new ItemGroup.Builder(null, -1);
        }
        builder.entries((displayContext, operatorEnabled) -> {});
        builder.icon(() -> ItemGroupBuilderImpl.this.icon.get());

        builder.displayName(Objects.requireNonNullElseGet(this.displayName, () -> TextUtil.translatable("itemGroup." + this.identifier.toString().replace(":", "."))));
        if (this.texture != null) builder.texture(this.texture);

        ItemGroup group = builder.build();
        ItemGroupHelper.addItemGroupInjection(group, (enabledFeatures, operatorEnabled, entriesImpl) -> {
            DarkMatterEntriesImpl entries1 = new DarkMatterEntriesImpl(entriesImpl);
            this.entries.collect(entries1);
        });
        if (this.animatedIcon != null) group.dm$setIconAnimation(this.animatedIcon);
        return group;
    }

}
