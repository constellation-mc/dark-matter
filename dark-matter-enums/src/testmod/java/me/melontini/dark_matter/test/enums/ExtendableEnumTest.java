package me.melontini.dark_matter.test.enums;

import java.util.Objects;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.api.base.util.MakeSure;
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

public class ExtendableEnumTest implements ModInitializer {
  @Override
  public void onInitialize() {
    AbstractMinecartEntity.Type category =
        ExtendableEnum.extend(AbstractMinecartEntity.Type.class, "INT_NAME");
    Objects.requireNonNull(category);

    Rarity rarity = ExtendableEnum.extend(Rarity.class, "TESTINGLY_RARE", () -> Formatting.BOLD);
    Objects.requireNonNull(rarity);
    MakeSure.isTrue(rarity.formatting == Formatting.BOLD);

    EnchantmentTarget target =
        ExtendableEnum.extend(EnchantmentTarget.class, "COOL_TARGET", () -> Item::isFood);
    Objects.requireNonNull(target);
    MakeSure.isTrue(target.isAcceptableItem(Items.COOKED_CHICKEN));

    BoatEntity.Type boatType = ExtendableEnum.extend(
        BoatEntity.Type.class,
        "REAL_WOOD",
        new Parameters.BoatEntityType(Blocks.AMETHYST_BLOCK, "amethyst"));
    Objects.requireNonNull(boatType);

    Formatting formatting = ExtendableEnum.extend(
        Formatting.class,
        "IDK",
        new Parameters.Formatting("IDK", 'p', 16, ColorUtil.toColor(193, 0, 184)));
    Objects.requireNonNull(formatting);
  }
}
