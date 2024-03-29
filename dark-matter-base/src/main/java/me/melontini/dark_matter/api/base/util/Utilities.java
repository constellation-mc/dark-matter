package me.melontini.dark_matter.api.base.util;

import lombok.experimental.UtilityClass;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.*;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@UtilityClass
public final class Utilities {

    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private static final BooleanSupplier TRUTH = () -> true;
    private static final BooleanSupplier FALSE = () -> false;

    public static <T> T pickAtRandom(@NotNull T[] list) {
        MakeSure.notEmpty(list);
        return list[MathUtil.threadRandom().nextInt(list.length)];
    }

    public static <T> T pickAtRandom(@NotNull List<T> list) {
        MakeSure.notEmpty(list);
        return list.get(MathUtil.threadRandom().nextInt(list.size()));
    }

    public static BooleanSupplier getTruth() {
        return TRUTH;
    }

    public static BooleanSupplier getFalse() {
        return FALSE;
    }

    public static <F, U> U cast(F o) {
        return (U) o;
    }

    public static <T> T supply(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T supply(T obj, Consumer<T> consumer) {
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
        return STACK_WALKER.walk(s -> s.skip(depth).findFirst().map(f -> f.getClassName() + "#" + f.getMethodName()));
    }

    public static Class<?> getCallerClass() {
        return getCallerClass(3).orElseThrow();
    }

    public static Optional<Class<?>> getCallerClass(int depth) {
        return STACK_WALKER.walk(s -> s.skip(depth).findFirst().map(StackWalker.StackFrame::getDeclaringClass));
    }

    public static <T> T makeLambda(MethodHandles.Lookup lookup, Class<T> type, MethodHandle h) {
        CallSite site = Exceptions.supply(() -> LambdaMetafactory.metafactory(lookup, "invoke",
                MethodType.methodType(type), h.type(), h, h.type()));
        return Exceptions.supply(() -> (T) site.getTarget().invoke());
    }
}
