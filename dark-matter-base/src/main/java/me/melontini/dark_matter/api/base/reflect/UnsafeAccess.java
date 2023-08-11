package me.melontini.dark_matter.api.base.reflect;

import me.melontini.dark_matter.impl.base.reflect.UnsafeInternals;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.*;

public class UnsafeAccess {
    private UnsafeAccess() {
        throw new UnsupportedOperationException();
    }

    /**
     * You can use this method to write private final fields.
     *
     * @param o this is the Object which contains the field. you should provide a class if your field is static
     */
    public static void putObject(Field field, Object o, Object value) {
        UnsafeInternals.putObject(field, o, value);
    }

    /**
     * You can use this method to read private fields.
     *
     * @param o this is the Object which contains the field. you should provide a class if your field is static
     */
    public static Object getObject(Field field, Object o) {
        return UnsafeInternals.getObject(field, o);
    }

    /**
     * free {@link Unsafe} for everyone!
     */
    public static Unsafe getUnsafe() {
        return UnsafeInternals.getUnsafe();
    }

    /**
     * Attempts to access the {@link jdk.internal.misc.Unsafe} object.
     *
     * @return the internal Unsafe, or null if it cannot be accessed
     * @throws RuntimeException if an error occurs while trying to access the internal Unsafe object
     */
    @SuppressWarnings("JavadocReference")
    public static @Nullable Object internalUnsafe() {
        return UnsafeInternals.internalUnsafe();
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
        return UnsafeInternals.getObjectFieldOffset(clazz, name);
    }
}
