package me.melontini.dark_matter.api.base.util;

import java.lang.invoke.*;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class Utilities {

  private static final StackWalker STACK_WALKER =
      StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  public static <T> T pickAtRandom(T @NotNull [] list) {
    return list[MathUtil.threadRandom().nextInt(MakeSure.notEmpty(list).length)];
  }

  public static <T> T pickAtRandom(@NotNull List<T> list) {
    return list.get(MathUtil.threadRandom().nextInt(MakeSure.notEmpty(list).size()));
  }

  @Contract(pure = true)
  public static @NotNull BooleanSupplier getTruth() {
    return () -> true;
  }

  @Contract(pure = true)
  public static @NotNull BooleanSupplier getFalse() {
    return () -> false;
  }

  public static <F, U> U cast(F o) {
    return (U) o;
  }

  public static <T> T supply(@NotNull Supplier<T> supplier) {
    return supplier.get();
  }

  @Contract("_, _ -> param1")
  public static <T> T supply(T obj, @NotNull Consumer<T> consumer) {
    consumer.accept(obj);
    return obj;
  }

  public static boolean isDev() {
    return FabricLoader.getInstance().isDevelopmentEnvironment();
  }

  public static String getCallerName() {
    return getCallerName(3).orElseThrow();
  }

  public static Optional<String> getCallerName(int depth) {
    return STACK_WALKER.walk(
        s -> s.skip(depth).findFirst().map(f -> f.getClassName() + "#" + f.getMethodName()));
  }

  public static Class<?> getCallerClass() {
    return getCallerClass(3).orElseThrow();
  }

  public static Optional<Class<?>> getCallerClass(int depth) {
    return STACK_WALKER.walk(
        s -> s.skip(depth).findFirst().map(StackWalker.StackFrame::getDeclaringClass));
  }

  public static StackWalker.StackFrame getCallerFrame() {
    return getCallerFrame(3).orElseThrow();
  }

  public static Optional<StackWalker.StackFrame> getCallerFrame(int depth) {
    return STACK_WALKER.walk(s -> s.skip(depth).findFirst());
  }

  public static <T> T makeLambda(MethodHandles.Lookup lookup, Class<T> type, MethodHandle handle) {
    return makeLambda(lookup, type, "invoke", handle);
  }

  @SneakyThrows
  public static <T> T makeLambda(
      MethodHandles.Lookup lookup, Class<T> type, String method, MethodHandle handle) {
    CallSite site = LambdaMetafactory.metafactory(
        lookup, method, MethodType.methodType(type), handle.type(), handle, handle.type());
    return (T) site.getTarget().invoke();
  }
}
