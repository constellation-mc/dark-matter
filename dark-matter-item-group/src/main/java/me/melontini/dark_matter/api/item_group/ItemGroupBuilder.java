package me.melontini.dark_matter.api.item_group;

import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.impl.item_group.ItemGroupBuilderImpl;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public interface ItemGroupBuilder {

    static ItemGroupBuilder create(Identifier id) {
        return new ItemGroupBuilderImpl(id);
    }

    default ItemGroupBuilder icon(ItemStack itemStack) {
        return this.icon(() -> itemStack);
    }

    default ItemGroupBuilder icon(ItemConvertible item) {
        return this.icon(new ItemStack(item));
    }

    ItemGroupBuilder icon(Supplier<ItemStack> itemStackSupplier);

    ItemGroupBuilder texture(String texture);

    ItemGroupBuilder entries(DarkMatterEntries.Collector collector);

    ItemGroupBuilder displayName(Text displayName);

    ItemGroupBuilder register(BooleanSupplier booleanSupplier);

    default ItemGroupBuilder register(boolean bool) {
        return register(bool ? Utilities.getTruth() : Utilities.getFalse());
    }

    Identifier getId();

    @Nullable ItemGroup build();

    default Optional<ItemGroup> optional() {
        return Optional.ofNullable(build());
    }
}
