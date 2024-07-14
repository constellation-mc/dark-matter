package me.melontini.dark_matter.api.base.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.functions.ThrowingConsumer;
import me.melontini.dark_matter.api.base.util.functions.ThrowingFunction;
import me.melontini.dark_matter.api.base.util.functions.ThrowingRunnable;
import me.melontini.dark_matter.api.base.util.functions.ThrowingSupplier;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public static <E extends Throwable> Runnable runnable(ThrowingRunnable<E> runnable) {
        return () -> run(runnable);
    }

    public static <T, E extends Throwable> Supplier<T> supplier(ThrowingSupplier<T, E> supplier) {
        return () -> supply(supplier);
    }

    public static <T, E extends Throwable> Consumer<T> consumer(ThrowingConsumer<T, E> consumer) {
        return t -> consume(t, consumer);
    }

    public static <T, R, E extends Throwable> Function<T, R> function(ThrowingFunction<T, R, E> function) {
        return t -> process(t, function);
    }

    @ApiStatus.Experimental
    public static Result<Void, Throwable> runAsResult(ThrowingRunnable<Throwable> runnable) {
        try {
            runnable.run();
            return Result.empty();
        } catch (Throwable e) {
            return Result.error(e);
        }
    }

    @ApiStatus.Experimental
    public static <T> Result<T, Throwable> supplyAsResult(ThrowingSupplier<T, Throwable> supplier) {
        try {
            return Result.ok(supplier.get());
        } catch (Throwable e) {
            return Result.error(e);
        }
    }

    @ApiStatus.Experimental
    public static <T> Result<T, Throwable> consumeAsResult(T obj, ThrowingConsumer<T, Throwable> consumer) {
        try {
            consumer.accept(obj);
            return Result.ok(obj);
        } catch (Throwable e) {
            return Result.error(e);
        }
    }

    @ApiStatus.Experimental
    public static <T, R> Result<R, Throwable> processAsResult(T obj, ThrowingFunction<T, R, Throwable> function) {
        try {
            return Result.ok(function.apply(obj));
        } catch (Throwable e) {
            return Result.error(e);
        }
    }

    public static RuntimeException wrap(Throwable t) {
        if (t instanceof RuntimeException re) return re;
        return new CompletionException(t);
    }
}
