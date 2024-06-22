package me.melontini.dark_matter.impl.item_group;

import lombok.NonNull;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.api.item_group.DarkMatterEntries;
import me.melontini.dark_matter.api.item_group.ItemGroupBuilder;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ItemGroupBuilderImpl implements ItemGroupBuilder {

    private final Identifier identifier;
    private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
    private Identifier texture;
    private DarkMatterEntries.Collector entries;
    private BooleanSupplier register = Utilities.getTruth();
    private Text displayName;

    public ItemGroupBuilderImpl(Identifier identifier) {
        if (!FabricLoader.getInstance().isModLoaded("fabric-item-group-api-v1")) DarkMatterLog.warn("Building {} ItemGroup without Fabric Item Groups", identifier);
        this.identifier = identifier;
    }

    @Override
    public ItemGroupBuilder icon(@NonNull Supplier<ItemStack> itemStackSupplier) {
        this.icon = itemStackSupplier;
        return this;
    }

    @Override
    public ItemGroupBuilder texture(@NonNull Identifier texture) {
        this.texture = texture;
        return this;
    }

    @Override
    public ItemGroupBuilder entries(@NonNull DarkMatterEntries.Collector collector) {
        this.entries = collector;
        return this;
    }

    @Override
    public ItemGroupBuilder displayName(@NonNull Text displayName) {
        this.displayName = displayName;
        return this;
    }


    @Override
    public ItemGroupBuilder register(@NonNull BooleanSupplier booleanSupplier) {
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

        ItemGroup.Builder builder = new ItemGroup.Builder(null, -1);
        builder.entries((displayContext, operatorEnabled) -> {});
        builder.icon(() -> ItemGroupBuilderImpl.this.icon.get());

        builder.displayName(Objects.requireNonNullElseGet(this.displayName, () -> Text.translatable("itemGroup." + this.identifier.toString().replace(':', '.'))));
        if (this.texture != null) builder.texture(this.texture);
        builder.entries((displayContext, entries1) -> this.entries.collect(new DarkMatterEntriesImpl(entries1)));

        ItemGroup group = builder.build();
        Registry.register(Registries.ITEM_GROUP, this.identifier, group);
        return group;
    }

}
