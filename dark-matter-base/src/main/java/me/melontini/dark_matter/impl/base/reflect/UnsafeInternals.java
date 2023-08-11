package me.melontini.dark_matter.impl.base.reflect;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.base.util.Utilities;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.*;

import static me.melontini.dark_matter.api.base.reflect.ReflectionUtil.setAccessible;

@ApiStatus.Internal
public class UnsafeInternals {
    private UnsafeInternals() {
        throw new UnsupportedOperationException();
    }

    private static final Unsafe UNSAFE = Utilities.supply(() -> {
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
    private static Object internalUnsafe;
    private static Method objectFieldOffset;

    public static void putObject(Field field, Object o, Object value) {
        long l = Modifier.isStatic(field.getModifiers()) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            UNSAFE.putObjectVolatile(o, l, value);
        } else {
            UNSAFE.putObject(o, l, value);
        }
    }

    public static Object getObject(Field field, Object o) {
        long l = Modifier.isStatic(field.getModifiers()) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            return UNSAFE.getObjectVolatile(o, l);
        } else {
            return UNSAFE.getObject(o, l);
        }
    }

    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    @SuppressWarnings("JavadocReference")
    public static @Nullable Object internalUnsafe() {
        if (internalUnsafe == null) {
            try {
                Field f2 = Unsafe.class.getDeclaredField("theInternalUnsafe");
                internalUnsafe = setAccessible(f2).get(null);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't access internal Unsafe", e);
            }
        }
        return internalUnsafe;
    }

    public static long getObjectFieldOffset(Class<?> clazz, String name) {
        try {
            if (objectFieldOffset == null) {
                Method method = MakeSure.notNull(internalUnsafe()).getClass().getDeclaredMethod("objectFieldOffset", Class.class, String.class);
                objectFieldOffset = setAccessible(method);
            }
            return (long) objectFieldOffset.invoke(internalUnsafe(), clazz, name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
