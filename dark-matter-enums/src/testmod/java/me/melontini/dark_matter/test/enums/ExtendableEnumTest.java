package me.melontini.dark_matter.test.enums;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.enums.Parameters;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

public class ExtendableEnumTest implements ModInitializer {
    @Override
    public void onInitialize() {
        AbstractMinecartEntity.Type category = ExtendableEnum.extend(AbstractMinecartEntity.Type.class, "INT_NAME", Parameters.EMPTY);
        MakeSure.notNull(category);

        Rarity rarity = ExtendableEnum.extend(Rarity.class, "TESTINGLY_RARE", () -> Formatting.BOLD);
        MakeSure.notNull(rarity);
        MakeSure.isTrue(rarity.formatting == Formatting.BOLD);
    }
}
