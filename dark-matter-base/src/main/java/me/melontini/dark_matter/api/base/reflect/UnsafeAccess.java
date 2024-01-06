package me.melontini.dark_matter.api.base.reflect;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.base.reflect.UnsafeInternals;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@UtilityClass
public class UnsafeAccess {

    public static void putReference(Field field, Object o, Object value) {
        UnsafeInternals.setReference(field, o, value);
    }

    public static <T> T getReference(Field field, Object o) {
        return UnsafeInternals.getReference(field, o);
    }

    @Deprecated
    public static void putObject(Field field, Object o, Object value) {
        UnsafeInternals.setReference(field, o, value);
    }

    @Deprecated
    public static Object getObject(Field field, Object o) {
        return UnsafeInternals.getReference(field, o);
    }

    public static <T> T allocateInstance(Class<T> cls) throws InstantiationException {
        return UnsafeInternals.allocateInstance(cls);
    }

    /**
     * free {@link Unsafe} for everyone!
     */
    public static Unsafe getUnsafe() {
        return UnsafeInternals.getUnsafe();
    }
}
