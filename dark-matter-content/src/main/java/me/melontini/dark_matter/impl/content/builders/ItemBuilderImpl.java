package me.melontini.dark_matter.impl.content.builders;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.content.ContentBuilder;
import me.melontini.dark_matter.api.content.ItemGroupHelper;
import me.melontini.dark_matter.api.content.RegistryUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@ApiStatus.Internal
public class ItemBuilderImpl<T extends Item> implements ContentBuilder.ItemBuilder<T> {

    private final Identifier identifier;
    private final Supplier<T> itemSupplier;
    private BooleanSupplier register = () -> true;
    private ItemGroup itemGroup;

    public ItemBuilderImpl(Identifier id, Supplier<T> itemSupplier) {
        MakeSure.notNull(id, "null identifier provided.");
        MakeSure.notNull(itemSupplier, "couldn't build: " + id);

        this.identifier = id;
        this.itemSupplier = itemSupplier;
    }

    public ContentBuilder.ItemBuilder<T> registerCondition(BooleanSupplier booleanSupplier) {
        MakeSure.notNull(booleanSupplier, "couldn't build: " + identifier);
        this.register = booleanSupplier;
        return this;
    }

    public ContentBuilder.ItemBuilder<T> itemGroup(ItemGroup group) {
        this.itemGroup = group;
        return this;
    }

    public T build() {
        T item = RegistryUtil.createItem(this.register, this.identifier, this.itemSupplier);
        if (item != null && this.itemGroup != null) ItemGroupHelper.addItemGroupInjection(this.itemGroup, (enabledFeatures, operatorEnabled, entriesImpl) -> entriesImpl.add(item));
        return item;
    }

}
