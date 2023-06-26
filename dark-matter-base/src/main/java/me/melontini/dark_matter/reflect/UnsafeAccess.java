package me.melontini.dark_matter.reflect;

import me.melontini.dark_matter.util.MakeSure;
import me.melontini.dark_matter.util.Utilities;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.*;

import static me.melontini.dark_matter.reflect.ReflectionUtil.setAccessible;

public class UnsafeAccess {
    private UnsafeAccess() {
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

    /**
     * You can use this method to write private final fields.
     *
     * @param o this is the Object which contains the field. you should provide a class if your field is static
     */
    public static void putObject(Field field, Object o, Object value) {
        long l = Modifier.isStatic(field.getModifiers()) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            UNSAFE.putObjectVolatile(o, l, value);
        } else {
            UNSAFE.putObject(o, l, value);
        }
    }

    /**
     * You can use this method to read private fields.
     *
     * @param o this is the Object which contains the field. you should provide a class if your field is static
     */
    public static Object getObject(Field field, Object o) {
        long l = Modifier.isStatic(field.getModifiers()) ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);
        boolean isVolatile = Modifier.isVolatile(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
        if (isVolatile) {
            return UNSAFE.getObjectVolatile(o, l);
        } else {
            return UNSAFE.getObject(o, l);
        }
    }

    /**
     * free {@link Unsafe} for everyone!
     */
    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    /**
     * Attempts to access the {@link jdk.internal.misc.Unsafe} object.
     *
     * @return the internal Unsafe, or null if it cannot be accessed
     * @throws RuntimeException if an error occurs while trying to access the internal Unsafe object
     */
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

    /**
     * Gets the offset of the given field in the given class.
     *
     * @param clazz the class containing the field
     * @param name  the name of the field
     * @return the offset of the given field in the given class
     * @throws RuntimeException if an error occurs while trying to determine the field offset
     */
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
