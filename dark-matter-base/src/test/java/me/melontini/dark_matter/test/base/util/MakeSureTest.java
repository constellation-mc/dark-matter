package me.melontini.dark_matter.test.base.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import me.melontini.dark_matter.api.base.util.MakeSure;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MakeSureTest {

  @Test
  public void testNotNull() {
    Assertions.assertThatThrownBy(() -> MakeSure.notNull(null))
        .isInstanceOf(NullPointerException.class);

    Assertions.assertThatThrownBy(() -> MakeSure.notNull(null, "testNotNull fail message"))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("testNotNull fail message");

    Assertions.assertThatThrownBy(() -> MakeSure.notNull(null, (Supplier<?>) null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("supplier");

    Assertions.assertThatThrownBy(() -> MakeSure.notNull(null, () -> null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("supplier.get()");
  }

  @Test
  public void testNotNulls() {
    Assertions.assertThatThrownBy(() -> MakeSure.notNulls(new Object(), null, new Object()))
        .isInstanceOf(NullPointerException.class);

    Assertions.assertThatThrownBy(
            () -> MakeSure.notNulls("testNotNulls fail message", new Object(), null, new Object()))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("testNotNulls fail message");
  }

  @Test
  public void testIsTrue() {
    Assertions.assertThatThrownBy(() -> MakeSure.isTrue(false))
        .isInstanceOf(IllegalArgumentException.class);

    Assertions.assertThatThrownBy(() -> MakeSure.isTrue(false, "testIsTrue fail message"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("testIsTrue fail message");

    Assertions.assertThatThrownBy(() -> MakeSure.isTrue(new Object(), Objects::isNull))
        .isInstanceOf(IllegalArgumentException.class);

    Assertions.assertThatThrownBy(
            () -> MakeSure.isTrue(new Object(), Objects::isNull, "testIsTrue fail message"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("testIsTrue fail message");
  }

  @Test
  public void testArrayNotEmpty() {
    Assertions.assertThatThrownBy(() -> MakeSure.notEmpty(new Object[0]))
        .isInstanceOf(IllegalArgumentException.class);

    Assertions.assertThatThrownBy(
            () -> MakeSure.notEmpty(new Object[0], "testArrayNotEmpty fail message"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("testArrayNotEmpty fail message");
  }

  @Test
  public void testCollectionNotEmpty() {
    Assertions.assertThatThrownBy(() -> MakeSure.notEmpty(List.of()))
        .isInstanceOf(IllegalArgumentException.class);

    Assertions.assertThatThrownBy(
            () -> MakeSure.notEmpty(List.of(), "testCollectionNotEmpty fail message"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("testCollectionNotEmpty fail message");
  }
}
