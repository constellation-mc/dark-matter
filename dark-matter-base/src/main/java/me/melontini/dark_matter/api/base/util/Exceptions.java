package me.melontini.dark_matter.api.base.util;

import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.functions.ThrowingConsumer;
import me.melontini.dark_matter.api.base.util.functions.ThrowingFunction;
import me.melontini.dark_matter.api.base.util.functions.ThrowingRunnable;
import me.melontini.dark_matter.api.base.util.functions.ThrowingSupplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class Exceptions {

  private static final IdentityHashMap<Class<?>, Function<Throwable, Throwable>> UNWRAPPERS =
      new IdentityHashMap<>();

  static {
    registerUnwrapper(CompletionException.class, Throwable::getCause);
    registerUnwrapper(UncheckedIOException.class, Throwable::getCause);
    registerUnwrapper(ExecutionException.class, Throwable::getCause);
    registerUnwrapper(InvocationTargetException.class, Throwable::getCause);
  }

  /**
   * Registers a new unwrapper for the {@link Exceptions#unwrap(Throwable)} method.
   *
   * @param type The class object of the throwable type.
   * @param unwrapper The function which will unwrap the exception.
   */
  public static synchronized <E extends Throwable> void registerUnwrapper(
      Class<E> type, Function<E, Throwable> unwrapper) {
    UNWRAPPERS.put(type, (Function<Throwable, Throwable>) unwrapper);
  }

  @SneakyThrows
  public static <E extends Throwable> void run(@NotNull ThrowingRunnable<E> runnable) {
    runnable.run();
  }

  @SneakyThrows
  public static <T, E extends Throwable> T supply(@NotNull ThrowingSupplier<T, E> supplier) {
    return supplier.get();
  }

  @Contract("_, _ -> param1")
  @SneakyThrows
  public static <T, E extends Throwable> T consume(
      T obj, @NotNull ThrowingConsumer<T, E> consumer) {
    consumer.accept(obj);
    return obj;
  }

  @SneakyThrows
  public static <T, R, E extends Throwable> R process(
      T obj, @NotNull ThrowingFunction<T, R, E> function) {
    return function.apply(obj);
  }

  @Contract(pure = true)
  public static <E extends Throwable> @NotNull Runnable runnable(ThrowingRunnable<E> runnable) {
    return () -> run(runnable);
  }

  @Contract(pure = true)
  public static <T, E extends Throwable> @NotNull Supplier<T> supplier(
      ThrowingSupplier<T, E> supplier) {
    return () -> supply(supplier);
  }

  @Contract(pure = true)
  public static <T, E extends Throwable> @NotNull Consumer<T> consumer(
      ThrowingConsumer<T, E> consumer) {
    return t -> consume(t, consumer);
  }

  @Contract(pure = true)
  public static <T, R, E extends Throwable> @NotNull Function<T, R> function(
      ThrowingFunction<T, R, E> function) {
    return t -> process(t, function);
  }

  @ApiStatus.Experimental
  public static <E extends Throwable> Result<Void, E> runAsResult(
      Class<E> type, ThrowingRunnable<? super E> runnable) {
    try {
      runnable.run();
      return Result.empty();
    } catch (Throwable e) {
      if (type.isInstance(e)) return Result.error(type.cast(e));
      return throwNow(e);
    }
  }

  @ApiStatus.Experimental
  public static <T, E extends Throwable> Result<T, E> supplyAsResult(
      Class<E> type, ThrowingSupplier<? extends T, ? extends E> supplier) {
    try {
      return Result.ok(supplier.get());
    } catch (Throwable e) {
      if (type.isInstance(e)) return Result.error(type.cast(e));
      return throwNow(e);
    }
  }

  @ApiStatus.Experimental
  public static <T, E extends Throwable> Result<T, E> consumeAsResult(
      Class<E> type, T obj, ThrowingConsumer<? super T, ? extends E> consumer) {
    try {
      consumer.accept(obj);
      return Result.ok(obj);
    } catch (Throwable e) {
      if (type.isInstance(e)) return Result.error(type.cast(e));
      return throwNow(e);
    }
  }

  @ApiStatus.Experimental
  public static <T, R, E extends Throwable> Result<R, E> processAsResult(
      Class<E> type, T obj, ThrowingFunction<? super T, ? extends R, ? extends E> function) {
    try {
      return Result.ok(function.apply(obj));
    } catch (Throwable e) {
      if (type.isInstance(e)) return Result.error(type.cast(e));
      return throwNow(e);
    }
  }

  @ApiStatus.Experimental
  public static Result<Void, Throwable> runAsResult(ThrowingRunnable<Throwable> runnable) {
    return runAsResult(Throwable.class, runnable);
  }

  @ApiStatus.Experimental
  public static <T> Result<T, Throwable> supplyAsResult(
      ThrowingSupplier<? extends T, Throwable> supplier) {
    return supplyAsResult(Throwable.class, supplier);
  }

  @ApiStatus.Experimental
  public static <T> Result<T, Throwable> consumeAsResult(
      T obj, ThrowingConsumer<? super T, Throwable> consumer) {
    return consumeAsResult(Throwable.class, obj, consumer);
  }

  @ApiStatus.Experimental
  public static <T, R> Result<R, Throwable> processAsResult(
      T obj, ThrowingFunction<? super T, ? extends R, Throwable> function) {
    return processAsResult(Throwable.class, obj, function);
  }

  /**
   * Unwraps a throwable from common wrapper exception types.
   * This operation is recursive and will unwrap all the causes.
   *
   * <p> Additional unwrappers can be registered using {@link Exceptions#registerUnwrapper(Class, Function)}.
   *
   * @param throwable The throwable to be unwrapped.
   * @return The unwrapped root cause or throwable if not wrapped.
   */
  public static Throwable unwrap(Throwable throwable) {
    for (Map.Entry<Class<?>, Function<Throwable, Throwable>> entry : UNWRAPPERS.entrySet()) {
      if (!entry.getKey().isInstance(throwable)) continue;

      var unwrapped = entry.getValue().apply(throwable);
      if (unwrapped != null) return unwrap(unwrapped);
    }
    return throwable;
  }

  /**
   * Wraps the passed throwable in a {@link CompletionException}.
   * If the throwable is already unchecked, returns the throwable.
   * @param throwable The throwable to be wrapped.
   * @return {@link CompletionException} wrapping the throwable or the throwable itself.
   */
  public static RuntimeException wrap(Throwable throwable) {
    if (throwable instanceof RuntimeException re) return re;
    return new CompletionException(throwable);
  }

  /**
   * Silently throws the passed throwable. Can be used in lambda functions if the parameter is a {@link Throwable}
   * @param throwable the throwable to be thrown.
   * @return Nothing.
   */
  @Contract(value = "_ -> fail", pure = true)
  @SneakyThrows
  public static <T> T throwNow(@NotNull Throwable throwable) {
    throw throwable;
  }
}
