package me.melontini.dark_matter.api.base.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class for ensuring that certain conditions are true, throwing an exception if not.
 */
@UtilityClass
@SuppressWarnings("UnusedReturnValue")
public final class MakeSure {

  @Contract(value = "null -> fail; !null -> param1", pure = true)
  public static <T> @NotNull T notNull(@Nullable T thing) {
    return Objects.requireNonNull(thing);
  }

  @Contract(value = "null, _ -> fail; !null, _ -> param1", pure = true)
  public static <T> @NotNull T notNull(@Nullable T thing, String msg) {
    return Objects.requireNonNull(thing, msg);
  }

  public static <T> @NotNull T notNull(@Nullable T thing, Supplier<T> supplier) {
    return Objects.requireNonNullElseGet(thing, supplier);
  }

  public static <T> @NotNull T notNull(@Nullable T thing, Supplier<T> supplier, String msg) {
    return Objects.isNull(thing) ? Objects.requireNonNull(supplier.get(), msg) : thing;
  }

  @Contract(value = "null -> fail", pure = true)
  public static void notNulls(@Nullable Object @Nullable ... things) {
    for (Object thing : Objects.requireNonNull(things)) Objects.requireNonNull(thing);
  }

  @Contract(value = "_, null -> fail", pure = true)
  public static void notNulls(String msg, @Nullable Object... things) {
    for (Object thing : Objects.requireNonNull(things)) Objects.requireNonNull(thing, msg);
  }

  @Contract(value = "false -> fail", pure = true)
  public static void isTrue(boolean bool) {
    if (!bool) throw new IllegalArgumentException();
  }

  @Contract(value = "false, _ -> fail", pure = true)
  public static void isTrue(boolean bool, String msg) {
    if (!bool) throw new IllegalArgumentException(msg);
  }

  @Contract("_, null -> fail")
  public static <T> T isTrue(T obj, Predicate<T> predicate) {
    if (predicate == null || !predicate.test(obj)) throw new IllegalArgumentException();
    return obj;
  }

  @Contract("_, null, _ -> fail")
  public static <T> T isTrue(T obj, Predicate<T> predicate, String msg) {
    if (predicate == null || !predicate.test(obj)) throw new IllegalArgumentException(msg);
    return obj;
  }

  @Contract(value = "null -> fail", pure = true)
  public static <T> T @NotNull [] notEmpty(T @Nullable [] array) {
    if (array == null || array.length == 0) throw new IllegalArgumentException();
    return array;
  }

  @Contract(value = "null, _ -> fail", pure = true)
  public static <T> T @NotNull [] notEmpty(T @Nullable [] array, String msg) {
    if (array == null || array.length == 0) throw new IllegalArgumentException(msg);
    return array;
  }

  @Contract("null -> fail")
  public static <T extends Collection<?>> @NotNull T notEmpty(@Nullable T collection) {
    if (collection == null || collection.isEmpty()) throw new IllegalArgumentException();
    return collection;
  }

  @Contract("null, _ -> fail")
  public static <T extends Collection<?>> @NotNull T notEmpty(@Nullable T collection, String msg) {
    if (collection == null || collection.isEmpty()) throw new IllegalArgumentException(msg);
    return collection;
  }

  @Contract("null -> fail")
  public static <T extends Map<?, ?>> @NotNull T notEmpty(@Nullable T map) {
    if (map == null || map.isEmpty()) throw new IllegalArgumentException();
    return map;
  }

  @Contract("null, _ -> fail")
  public static <T extends Map<?, ?>> @NotNull T notEmpty(@Nullable T map, String msg) {
    if (map == null || map.isEmpty()) throw new IllegalArgumentException(msg);
    return map;
  }

  @Contract("null -> fail")
  public static @NotNull String notEmpty(@Nullable String string) {
    if (string == null || string.isEmpty()) throw new IllegalArgumentException();
    return string;
  }

  @Contract("null, _ -> fail")
  public static @NotNull String notEmpty(@Nullable String string, String msg) {
    if (string == null || string.isEmpty()) throw new IllegalArgumentException(msg);
    return string;
  }

  public static @NotNull String notEmpty(
      @Nullable String string, @NotNull Supplier<String> supplier) {
    String ret = string == null || string.isEmpty() ? supplier.get() : string;
    if (ret == null || ret.isEmpty()) throw new IllegalArgumentException();
    return ret;
  }

  public static @NotNull String notEmpty(
      @Nullable String string, @NotNull Supplier<String> supplier, String msg) {
    String ret = string == null || string.isEmpty() ? supplier.get() : string;
    if (ret == null || ret.isEmpty()) throw new IllegalArgumentException(msg);
    return ret;
  }

  @Deprecated(since = "2.0.0")
  @Contract(value = "true -> fail", pure = true)
  public static void isFalse(boolean bool) {
    if (bool) throw new IllegalArgumentException();
  }

  @Deprecated(since = "2.0.0")
  @Contract(value = "true, _ -> fail", pure = true)
  public static void isFalse(boolean bool, String msg) {
    if (bool) throw new IllegalArgumentException(msg);
  }

  @Deprecated(since = "2.0.0")
  public static <T> T isFalse(T obj, Predicate<T> predicate) {
    if (predicate.test(obj)) throw new IllegalArgumentException();
    return obj;
  }

  @Deprecated(since = "2.0.0")
  public static <T> T isFalse(T obj, Predicate<T> predicate, String msg) {
    if (predicate.test(obj)) throw new IllegalArgumentException(msg);
    return obj;
  }
}
