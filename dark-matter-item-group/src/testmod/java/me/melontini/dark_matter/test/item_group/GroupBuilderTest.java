package me.melontini.dark_matter.test.item_group;

import me.melontini.dark_matter.api.item_group.ItemGroupBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.assertj.core.api.Assertions;

public class GroupBuilderTest implements ModInitializer {

  public static ItemGroup group;

  @Override
  public void onInitialize() {
    group = ItemGroupBuilder.create(new Identifier("dark-matter", "test_group"))
        .entries(entries -> entries.add(Items.GLOW_ITEM_FRAME))
        .icon(Items.BLUE_ORCHID)
        .build();

    Assertions.assertThat(group)
        .isNotNull()
        .matches(group -> !group.isSpecial())
        .matches(group -> group.getIcon().getItem() == Items.BLUE_ORCHID)
        .extracting(ItemGroup::getDisplayName)
        .isNotNull();
  }
}
