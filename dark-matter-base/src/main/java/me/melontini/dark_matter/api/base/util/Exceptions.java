package me.melontini.dark_matter.api.base.util;

import lombok.SneakyThrows;
import me.melontini.dark_matter.api.base.util.classes.ThrowingConsumer;
import me.melontini.dark_matter.api.base.util.classes.ThrowingFunction;
import me.melontini.dark_matter.api.base.util.classes.ThrowingRunnable;
import me.melontini.dark_matter.api.base.util.classes.ThrowingSupplier;

public class Exceptions {

    @SneakyThrows
    public static <E extends Throwable> void run(ThrowingRunnable<E> runnable) {
        runnable.run();
    }

    @SneakyThrows
    public static <T, E extends Throwable> T supply(ThrowingSupplier<T, E> supplier) {
        return supplier.get();
    }

    @SneakyThrows
    public static <T, E extends Throwable> T consume(T obj, ThrowingConsumer<T, E> consumer) {
        consumer.accept(obj);
        return obj;
    }

    @SneakyThrows
    public static <T, R, E extends Throwable> R process(T obj, ThrowingFunction<T, R, E> function) {
        return function.apply(obj);
    }
}
