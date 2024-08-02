package me.melontini.dark_matter.api.item_group;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import lombok.NonNull;
import me.melontini.dark_matter.api.base.util.Utilities;
import me.melontini.dark_matter.impl.item_group.ItemGroupBuilderImpl;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface ItemGroupBuilder {

  static ItemGroupBuilder create(@NonNull Identifier identifier) {
    return new ItemGroupBuilderImpl(identifier);
  }

  default ItemGroupBuilder icon(ItemStack itemStack) {
    return this.icon(() -> itemStack);
  }

  default ItemGroupBuilder icon(ItemConvertible item) {
    return this.icon(new ItemStack(item));
  }

  ItemGroupBuilder icon(Supplier<ItemStack> itemStackSupplier);

  default ItemGroupBuilder texture(String texture) {
    return this.texture(Identifier.of(texture));
  }

  ItemGroupBuilder texture(Identifier texture);

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
