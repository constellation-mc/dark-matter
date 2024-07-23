package me.melontini.dark_matter.test.base.util;

import me.melontini.dark_matter.api.base.util.MathUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class MathUtilTest {

  @CsvSource(
      delimiter = ':',
      value = {
        "10 : 5 : 12 : 10",
        "2  : 3 : 5  : 3",
        "50 : 1 : 3  : 3",
        "7  : 5 : 7  : 7",
        "2  : 2 : 67 : 2",
      })
  @ParameterizedTest
  public void testLongClamp(String value, String min, String max, String expected) {
    Assertions.assertThat(
            MathUtil.clamp(Long.parseLong(value), Long.parseLong(min), Long.parseLong(max)))
        .isEqualTo(Long.parseLong(expected));
  }

  @CsvSource(
      delimiter = ':',
      value = {
        "10   : 5   : 12 : 10",
        "2.5  : 3   : 5  : 3",
        "50   : 1.5 : 3  : 3",
        "7.5  : 5   : 7  : 7",
        "2.9  : 3   : 67 : 3",
      })
  @ParameterizedTest
  public void testDoubleClamp(String value, String min, String max, String expected) {
    Assertions.assertThat(MathUtil.clamp(
            Double.parseDouble(value), Double.parseDouble(min), Double.parseDouble(max)))
        .isEqualTo(Double.parseDouble(expected));
  }
}
