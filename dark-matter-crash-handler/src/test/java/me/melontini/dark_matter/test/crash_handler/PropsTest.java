package me.melontini.dark_matter.test.crash_handler;

import me.melontini.dark_matter.api.crash_handler.Props;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class PropsTest {

  @Test
  public void testPropsEnum() {
    for (Props prop : Props.values()) {
      Assertions.assertThat(prop.get()).isNotNull().isNotBlank();
    }
  }
}
