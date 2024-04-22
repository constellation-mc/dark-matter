package me.melontini.dark_matter.test.minecraft;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.minecraft.util.RegistryUtil;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Items;

import java.util.Objects;

public class RegistryUtilTest implements ModInitializer {
    @Override
    public void onInitialize() {
        MakeSure.isTrue(Objects.equals(RegistryUtil.asBlockEntity(Blocks.CHEST), BlockEntityType.CHEST), "RegistryUtil#asBlockEntity");
        MakeSure.isTrue(Objects.equals(RegistryUtil.asItem(Blocks.ACACIA_BUTTON), Items.ACACIA_BUTTON), "RegistryUtil#asItem");
    }
}
