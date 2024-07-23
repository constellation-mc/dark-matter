package me.melontini.dark_matter.test.base.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import me.melontini.dark_matter.api.base.util.functions.Memoize;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

public class MemoizeTest {

  @Test
  public void testMemoizeSupplier() {
    AtomicInteger counter = new AtomicInteger(0);
    Supplier<String> supplier = Memoize.supplier(() -> {
      counter.incrementAndGet();
      return "Hello Test World!";
    });

    for (int i = 0; i < 4; i++) {
      Assertions.assertThat(supplier)
          .extracting(Supplier::get, Assertions.as(InstanceOfAssertFactories.STRING))
          .isEqualTo("Hello Test World!");
    }
    Assertions.assertThat(counter).hasValue(1);
  }

  @Test
  public void testMemoizeFunction() {
    AtomicInteger counter = new AtomicInteger(0);
    Function<Integer, String> supplier = Memoize.function((i) -> {
      counter.incrementAndGet();
      return "Hello Test World! " + i;
    });

    for (int i = 0; i < 4; i++) {
      Assertions.assertThat(supplier)
          .extracting(func -> func.apply(2), Assertions.as(InstanceOfAssertFactories.STRING))
          .isEqualTo("Hello Test World! 2");
    }
    Assertions.assertThat(counter).hasValue(1);
  }
}
