package me.melontini.dark_matter.api.base.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.functions.ThrowingConsumer;
import me.melontini.dark_matter.api.base.util.functions.ThrowingFunction;
import me.melontini.dark_matter.api.base.util.functions.ThrowingRunnable;
import me.melontini.dark_matter.api.base.util.functions.ThrowingSupplier;

@UtilityClass
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
