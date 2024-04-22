package me.melontini.dark_matter.impl.base.reflect;

import com.google.common.base.Suppliers;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.reflect.UnsafeUtils;
import me.melontini.dark_matter.api.base.util.Utilities;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.function.Supplier;

import static me.melontini.dark_matter.api.base.util.Exceptions.supply;

@UtilityClass
@ApiStatus.Internal
public class UnsafeInternals {

    private static final Supplier<Unsafe> UNSAFE = Suppliers.memoize(() -> {
        try {
            Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
            unsafe.setAccessible(true);
            return (Unsafe) unsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Constructor<Unsafe> constructor = Unsafe.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException ex) {
                throw new RuntimeException("Couldn't access Unsafe", ex);
            }
        }
    });

    private static final Supplier<MethodHandles.Lookup> TRUSTED_LOOKUP = Suppliers.memoize(() -> supply(() -> UnsafeUtils.getReference(MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP"), null)));
    private static final Supplier<MethodHandle> DEFINE_CLASS = Suppliers.memoize(() -> supply(() -> TRUSTED_LOOKUP.get().findVirtual(ClassLoader.class, "defineClass", MethodType.methodType(Class.class, String.class, byte[].class, int.class, int.class, ProtectionDomain.class))));

    public static MethodHandles.@NotNull Lookup lookupIn(Class<?> cls) {
        return TRUSTED_LOOKUP.get().in(cls);
    }

    public static Class<?> defineClass(ClassLoader loader, String name, byte[] bytes, @Nullable ProtectionDomain domain) {
        return (Class<?>) supply(() -> DEFINE_CLASS.get().invoke(loader, name, bytes, domain));
    }

    public static void setReference(@NonNull Field field, Object o, Object value) {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            getUnsafe().putObjectVolatile(
                    isStatic ? getUnsafe().staticFieldBase(field) : o,
                    isStatic ? getUnsafe().staticFieldOffset(field) : getUnsafe().objectFieldOffset(field),
                    value);
        } else {
            getUnsafe().putObject(
                    isStatic ? getUnsafe().staticFieldBase(field) : o,
                    isStatic ? getUnsafe().staticFieldOffset(field) : getUnsafe().objectFieldOffset(field),
                    value);
        }
    }

    public static <T> T getReference(@NonNull Field field, @Nullable Object o) {
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            return Utilities.cast(getUnsafe().getObjectVolatile(
                    isStatic ? getUnsafe().staticFieldBase(field) : o,
                    isStatic ? getUnsafe().staticFieldOffset(field) : getUnsafe().objectFieldOffset(field)));
        } else {
            return Utilities.cast(getUnsafe().getObject(
                    isStatic ? getUnsafe().staticFieldBase(field) : o,
                    isStatic ? getUnsafe().staticFieldOffset(field) : getUnsafe().objectFieldOffset(field)));
        }
    }

    public static <T> T allocateInstance(@NonNull Class<T> cls) throws InstantiationException {
        return Utilities.cast(getUnsafe().allocateInstance(cls));
    }

    public static Unsafe getUnsafe() {
        return UNSAFE.get();
    }
}
