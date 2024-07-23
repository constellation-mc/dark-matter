package me.melontini.dark_matter.test.base.util;

import me.melontini.dark_matter.api.base.util.Exceptions;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletionException;

public class ExceptionsTest {

    @Test
    void supplyAsResultTest() {
        Assertions.assertThat(Exceptions.supplyAsResult(() -> "No Issues!"))
                .matches(result -> result.value().isPresent() && result.error().isEmpty())
                .extracting(result -> result.value().orElseThrow()).isEqualTo("No Issues!");

        Assertions.assertThat(Exceptions.supplyAsResult(() -> {
                    throw new Exception("Test exception!");
                }))
                .matches(result -> result.value().isEmpty() && result.error().isPresent())
                .extracting(r -> r.error().orElseThrow(), Assertions.as(InstanceOfAssertFactories.THROWABLE))
                .isOfAnyClassIn(Exception.class).hasMessage("Test exception!");
    }

    @Test
    void unwrapTest() {
        Exception parent = new Exception();
        CompletionException throwable = new CompletionException(parent);
        Assertions.assertThat(Exceptions.unwrap(throwable))
                        .isEqualTo(parent);
    }

    @Test
    void wrapTest() {
        Throwable throwable = new Throwable();
        Assertions.assertThat(Exceptions.wrap(throwable))
                .isInstanceOf(CompletionException.class);

        UncheckedIOException exception = new UncheckedIOException(new IOException());
        Assertions.assertThat(Exceptions.wrap(exception))
                        .isInstanceOf(UncheckedIOException.class);
    }
}
