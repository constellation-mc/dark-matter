package me.melontini.dark_matter.test.minecraft;

import me.melontini.dark_matter.api.minecraft.util.RegistryUtil;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.assertj.core.api.Assertions;

public class RegistryUtilTest implements ModInitializer {
  @Override
  public void onInitialize() {
    Assertions.assertThat(RegistryUtil.asBlockEntity(Blocks.CHEST))
        .isEqualTo(BlockEntityType.CHEST);

    Assertions.assertThat(RegistryUtil.<Item>asItem(Blocks.ACACIA_BUTTON))
        .isEqualTo(Items.ACACIA_BUTTON);
  }
}
