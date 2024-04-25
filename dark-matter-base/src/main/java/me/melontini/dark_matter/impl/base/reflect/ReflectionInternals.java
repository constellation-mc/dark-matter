package me.melontini.dark_matter.impl.base.reflect;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <a href="https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass">source</a>
 *
 * @author <a href="https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass">source</a>
 */
@UtilityClass
public class ReflectionInternals {

    public static @Nullable <T> Constructor<T> findConstructor(@NonNull Class<T> clazz, Class<?>... classes) {
        Constructor<T>[] ctxs = (Constructor<T>[]) clazz.getDeclaredConstructors();
        if (ctxs.length == 1) {
            return checkCtx(ctxs[0], classes) ? ctxs[0] : null;
        } else {
            try {
                return clazz.getDeclaredConstructor(classes);
            } catch (Exception e) {
                for (Constructor<T> ctx : ctxs) {
                    if (checkCtx(ctx, classes)) return ctx;
                }
            }
        }
        return null;
    }

    private static boolean checkCtx(@NonNull Constructor<?> ctx, Class<?>[] classes) {
        if (ctx.getParameterCount() != classes.length) return false;

        Class<?>[] pt = ctx.getParameterTypes();
        for (int i = 0; i < ctx.getParameterCount(); i++) {
            if (!ClassUtils.isAssignable(classes[i], pt[i])) {
                return false;
            }
        }
        return true;
    }

    public static @Nullable <T> Method findMethod(@NonNull Class<T> clazz, boolean traverse, String name, Class<?>... classes) {
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length == 1) {
            return checkMethod(methods[0], name, classes) ? methods[0] : null;
        } else {
            try {
                return clazz.getDeclaredMethod(name, classes);
            } catch (Throwable e) {
                for (Method method : methods) {
                    if (checkMethod(method, name, classes)) return method;
                }
            }
        }
        return traverse && clazz.getSuperclass() != null ? findMethod(clazz.getSuperclass(), true, name, classes) : null;
    }

    private static boolean checkMethod(@NonNull Method method, String name, Class<?>[] classes) {
        if (!method.getName().equals(name)) return false;
        if (method.getParameterCount() != classes.length) return false;

        Class<?>[] pt = method.getParameterTypes();
        for (int i = 0; i < method.getParameterCount(); i++) {
            if (!ClassUtils.isAssignable(classes[i], pt[i])) {
                return false;
            }
        }
        return true;
    }

    public static <T> @Nullable Field findField(@NonNull Class<T> clazz, boolean traverse, String name) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 1) {
            return fields[0].getName().equals(name) ? fields[0] : null;
        } else {
            for (Field field : fields) {
                if (field.getName().equals(name)) {
                    return field;
                }
            }
        }
        return traverse && clazz.getSuperclass() != null ? findField(clazz.getSuperclass(), true, name) : null;
    }

    public static <T extends AccessibleObject> T setAccessible(@NonNull T member, boolean set) {
        try {
            member.setAccessible(set);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return member;
    }
}
