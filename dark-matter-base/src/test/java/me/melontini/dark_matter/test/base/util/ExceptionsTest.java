package me.melontini.dark_matter.test.base.util;

import me.melontini.dark_matter.api.base.util.Exceptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletionException;

public class ExceptionsTest {

    @Test
    void supplyAsResultTest() {
        var test1 = Exceptions.supplyAsResult(() -> "No Issues!");
        Assertions.assertTrue(test1.value().isPresent() && test1.error().isEmpty());

        var test2 = Exceptions.supplyAsResult(() -> {
            throw new Exception();
        });
        Assertions.assertTrue(test2.value().isEmpty() && test2.error().isPresent());
    }

    @Test
    void unwrapTest() {
        Exception parent = new Exception();
        CompletionException throwable = new CompletionException(parent);
        Assertions.assertEquals(Exceptions.unwrap(throwable), parent);
    }

    @Test
    void wrapTest() {
        Throwable throwable = new Throwable();
        Assertions.assertInstanceOf(CompletionException.class, Exceptions.wrap(throwable));

        UncheckedIOException exception = new UncheckedIOException(new IOException());
        Assertions.assertInstanceOf(UncheckedIOException.class, Exceptions.wrap(exception));
    }
}
