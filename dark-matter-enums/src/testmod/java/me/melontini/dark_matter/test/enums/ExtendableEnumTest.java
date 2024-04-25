package me.melontini.dark_matter.test.enums;

import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.enums.Parameters;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

public class ExtendableEnumTest implements ModInitializer {
    @Override
    public void onInitialize() {
        AbstractMinecartEntity.Type category = ExtendableEnum.extend(AbstractMinecartEntity.Type.class, "INT_NAME");
        MakeSure.notNull(category);

        Rarity rarity = ExtendableEnum.extend(Rarity.class, "TESTINGLY_RARE", new Parameters.Rarity(4, "testingly_rare", Formatting.BOLD));
        MakeSure.notNull(rarity);
        MakeSure.isTrue(rarity.getFormatting() == Formatting.BOLD);

        BoatEntity.Type boatType = ExtendableEnum.extend(BoatEntity.Type.class, "REAL_WOOD", new Parameters.BoatEntityType(Blocks.AMETHYST_BLOCK, "amethyst"));
        MakeSure.notNull(boatType);

        Formatting formatting = ExtendableEnum.extend(Formatting.class, "IDK", new Parameters.Formatting("IDK", 'p', 16, ColorUtil.toColor(193, 0, 184)));
        MakeSure.notNull(formatting);
    }
}
