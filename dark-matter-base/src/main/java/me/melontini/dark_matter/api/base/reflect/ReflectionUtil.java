package me.melontini.dark_matter.api.base.reflect;

import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.base.reflect.ReflectionInternals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass">source</a>
 */
@UtilityClass
@SuppressWarnings("unused")
@Deprecated(forRemoval = true)
public class ReflectionUtil {

    /**
     * See {@link Reflect#findConstructor(Class, Object...)}
     */
    public static @Nullable <T> Constructor<T> findConstructor(Class<T> clazz, Object... args) {
        return Reflect.findConstructor(clazz, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new)).orElse(null);
    }

    /**
     * See {@link Reflect#findConstructor(Class, Class[])} (Class, List)}
     */
    public static @Nullable <T> Constructor<T> findConstructor(@NotNull Class<T> clazz, List<Object> args) {
        return Reflect.findConstructor(clazz, args.stream().map(Object::getClass).toArray(Class[]::new)).orElse(null);
    }

    /**
     * See {@link Reflect#findMethod(Class, String, Object...)}
     */
    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, Object... args) {
        return Reflect.findMethod(clazz, name, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new)).orElse(null);
    }

    /**
     * See {@link Reflect#findMethod(Class, String, Object...)} (Class, String, List)}
     */
    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, List<Object> args) {
        return Reflect.findMethod(clazz, name, args.stream().map(Object::getClass).toArray(Class[]::new)).orElse(null);
    }

    public static @Nullable <T> Field findField(@NotNull Class<T> clazz, String name) {
        return Reflect.findField(clazz, name).orElse(null);
    }

    /**
     * See {@link Reflect#setAccessible(AccessibleObject)}
     */
    public static <T> Constructor<T> setAccessible(Constructor<T> constructor) {
        return Reflect.setAccessible(constructor, true);
    }

    /**
     * See {@link Reflect#setAccessible(AccessibleObject)}
     */
    public static Method setAccessible(Method method) {
        return Reflect.setAccessible(method, true);
    }

    /**
     * See {@link Reflect#setAccessible(AccessibleObject)}
     */
    public static Field setAccessible(Field field) {
        return Reflect.setAccessible(field, true);
    }

    public static Object getField(Field field, Object o) {
        return ReflectionInternals.getField(field, o);
    }

    public static void setField(Field field, Object o, Object value) {
        ReflectionInternals.setField(field, o, value);
    }
}
