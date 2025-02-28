package me.melontini.dark_matter.api.base.util.functions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Memoize {

  @Contract(value = "_ -> new", pure = true)
  public static <T> @NotNull Supplier<T> supplier(Supplier<T> delegate) {
    return new Supplier<>() {
      T value;
      volatile boolean initialized;

      @Override
      public T get() {
        if (!initialized) {
          synchronized (this) {
            if (!initialized) {
              T t = delegate.get();
              this.value = t;
              this.initialized = true;
              return t;
            }
          }
        }
        return value;
      }
    };
  }

  /**
   * A simple memoizing function based on {@link HashMap}. <br/>
   * This implementation does not remove any of the entries.
   */
  @Contract("_ -> new")
  public static <T, R> @NotNull Function<T, R> function(Function<T, R> delegate) {
    return cachedFunction(new HashMap<>(), delegate);
  }

  /**
   * A simple memoizing function based on {@link IdentityHashMap}. <br/>
   * This implementation does not remove any of the entries.
   */
  @Contract("_ -> new")
  public static <T, R> @NotNull Function<T, R> identityFunction(Function<T, R> delegate) {
    return cachedFunction(new IdentityHashMap<>(), delegate);
  }

  /**
   * A simple memoizing function based on {@link LinkedHashMap}. <br/>
   * Unlike previous functions, this one will evict unused entries when capacity is too high.
   */
  @Contract("_, _ -> new")
  public static <T, R> @NotNull Function<T, R> lruFunction(Function<T, R> delegate, int capacity) {
    return cachedFunction(
        Collections.synchronizedMap(new LinkedHashMap<>(capacity + 1, 0.75f, true) {
          @Override
          protected boolean removeEldestEntry(Map.Entry<T, R> eldest) {
            return size() > capacity;
          }
        }),
        delegate);
  }

  @Contract(value = "_, _ -> new", pure = true)
  public static <T, R> @NotNull Function<T, R> cachedFunction(
      Map<T, R> cache, Function<T, R> delegate) {
    return new Function<>() {
      @Override
      public R apply(T t) {
        if (!cache.containsKey(t)) {
          synchronized (this) {
            if (!cache.containsKey(t)) {
              R r = delegate.apply(t);
              cache.put(t, r);
              return r;
            }
          }
        }
        return cache.get(t);
      }
    };
  }
}
