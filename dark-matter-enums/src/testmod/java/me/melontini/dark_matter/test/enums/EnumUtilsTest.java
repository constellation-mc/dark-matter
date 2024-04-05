package me.melontini.dark_matter.test.enums;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.enums.EnumUtils;
import net.fabricmc.api.ModInitializer;

public class EnumUtilsTest implements ModInitializer {
    @Override
    public void onInitialize() {
        TestEnum category = EnumUtils.getEnumConstant("CONSTANT", TestEnum.class);
        MakeSure.isTrue(category == TestEnum.CONSTANT);

        TestEnum newCat = EnumUtils.extendByReflecting(true, TestEnum.class, "TEST");
        MakeSure.notNull(newCat);

        category = EnumUtils.getEnumConstant("TEST", TestEnum.class);
        MakeSure.isTrue(category == newCat);
    }

    public enum TestEnum {
        CONSTANT
    }
}
