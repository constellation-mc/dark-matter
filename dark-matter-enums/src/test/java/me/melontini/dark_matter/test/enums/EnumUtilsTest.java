package me.melontini.dark_matter.test.enums;

import me.melontini.dark_matter.api.enums.EnumUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumUtilsTest {

  @Test
  public void getEnumConstant() {
    TestEnum category = EnumUtils.getEnumConstant("CONSTANT", TestEnum.class);
    Assertions.assertEquals(category, TestEnum.CONSTANT);
  }

  @Test
  public void extendByReflecting() {
    TestEnum newCat = EnumUtils.extendByReflecting(true, TestEnum.class, "TEST");
    Assertions.assertNotNull(newCat);
  }

  @Test
  public void getEnumConstantCacheReset() {
    TestEnum newCat = EnumUtils.extendByReflecting(true, TestEnum.class, "TEST2");
    TestEnum category = EnumUtils.getEnumConstant("TEST2", TestEnum.class);
    Assertions.assertEquals(category, newCat);
  }

  public enum TestEnum {
    CONSTANT
  }
}
