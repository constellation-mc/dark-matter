package me.melontini.dark_matter.test.enums;

import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.api.enums.Parameters;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import org.assertj.core.api.Assertions;

public class ExtendableEnumTest implements ModInitializer {
  @Override
  public void onInitialize() {
    AbstractMinecartEntity.Type category =
        ExtendableEnum.extend(AbstractMinecartEntity.Type.class, "INT_NAME");
    Assertions.assertThat(category).isNotNull();

    Rarity rarity = ExtendableEnum.extend(Rarity.class, "TESTINGLY_RARE", () -> Formatting.BOLD);
    Assertions.assertThat(rarity)
        .isNotNull()
        .matches(rarity1 -> rarity1.formatting == Formatting.BOLD, "formatting is `BOLD`");

    EnchantmentTarget target =
        ExtendableEnum.extend(EnchantmentTarget.class, "COOL_TARGET", () -> Item::isFood);
    Assertions.assertThat(target)
        .isNotNull()
        .matches(et -> et.isAcceptableItem(Items.COOKED_CHICKEN), "predicate allows food");

    BoatEntity.Type boatType = ExtendableEnum.extend(
        BoatEntity.Type.class,
        "REAL_WOOD",
        new Parameters.BoatEntityType(Blocks.AMETHYST_BLOCK, "amethyst"));
    Assertions.assertThat(boatType)
        .isNotNull()
        .matches(
            type -> Blocks.AMETHYST_BLOCK.equals(type.getBaseBlock()),
            "block is 'minecraft:amethyst'")
        .matches(type -> "amethyst".equals(type.getName()), "name is amethyst");

    Formatting formatting = ExtendableEnum.extend(
        Formatting.class,
        "IDK",
        new Parameters.Formatting("IDK", 'p', 16, ColorUtil.toColor(193, 0, 184)));
    Assertions.assertThat(formatting)
        .isNotNull()
        .matches(f -> "IDK".equals(f.name()), "name is IDK")
        .matches(f -> 'p' == f.getCode(), "code is 'p'")
        .matches(f -> Integer.valueOf(16).equals(f.getColorIndex()), "colorIndex is 16")
        .matches(
            f -> Integer.valueOf(ColorUtil.toColor(193, 0, 184)).equals(f.getColorValue()),
            "color is [193, 0, 184]");
  }
}
