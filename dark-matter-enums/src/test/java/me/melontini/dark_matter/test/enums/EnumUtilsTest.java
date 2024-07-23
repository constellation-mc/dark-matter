package me.melontini.dark_matter.test.enums;

import me.melontini.dark_matter.api.enums.EnumUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumUtilsTest {

  @Test
  public void getEnumConstant() {
    Assertions.assertThat(EnumUtils.getEnumConstant("CONSTANT", TestEnum.class))
        .isNotNull()
        .isEqualTo(TestEnum.CONSTANT);
  }

  @Test
  public void extendByReflecting() {
    Assertions.assertThat(EnumUtils.extendByReflecting(true, TestEnum.class, "TEST"))
        .isNotNull();
  }

  @Test
  public void getEnumConstantCacheReset() {
    Assertions.assertThat(EnumUtils.extendByReflecting(true, TestEnum.class, "TEST2"))
        .isNotNull()
        .isEqualTo(EnumUtils.getEnumConstant("TEST2", TestEnum.class));
  }

  public enum TestEnum {
    CONSTANT
  }
}
