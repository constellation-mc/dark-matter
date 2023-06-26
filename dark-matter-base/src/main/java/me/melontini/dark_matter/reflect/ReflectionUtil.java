package me.melontini.dark_matter.reflect;

import me.melontini.dark_matter.DarkMatterLog;
import me.melontini.dark_matter.util.MakeSure;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass">source</a>
 */
public class ReflectionUtil {
    private ReflectionUtil() {
        throw new UnsupportedOperationException();
    }
    private static int offset = -1;

    /**
     * Attempts to find a constructor for the given class that matches the given array of arguments.
     *
     * @param clazz the class for which to find a constructor
     * @param args  the array of arguments that the constructor must be able to accept
     * @return a constructor for the given class that matches the given array of arguments, or null if no such constructor is found
     */
    public static @Nullable <T> Constructor<T> findConstructor(Class<T> clazz, Object... args) {
        return findConstructor(clazz, Arrays.stream(args).toList());
    }

    /**
     * Attempts to find a constructor for the given class that matches the given list of arguments.
     *
     * @param clazz the class for which to find a constructor
     * @param args  the list of arguments that the constructor must be able to accept
     * @return a constructor for the given class that matches the given list of arguments, or null if no such constructor is found
     */
    public static @Nullable <T> Constructor<T> findConstructor(@NotNull Class<T> clazz, List<Object> args) {
        Constructor<T> c = null;

        Constructor<T>[] ctxs = (Constructor<T>[]) clazz.getDeclaredConstructors();
        if (clazz.getDeclaredConstructors().length == 1) {
            c = ctxs[0];// we can skip loops if there's only 1 constructor in a class.
        } else {
            Class<?>[] classes = args.stream().map(Object::getClass).toArray(Class[]::new);

            try {
                c = clazz.getDeclaredConstructor(classes);
            } catch (Exception e) {
                for (Constructor<T> declaredConstructor : ctxs) {
                    if (declaredConstructor.getParameterCount() != args.size()) continue;

                    boolean bool = true;
                    Class<?>[] pt = declaredConstructor.getParameterTypes();
                    for (int i = 0; i < declaredConstructor.getParameterCount(); i++) {
                        if (!ClassUtils.isAssignable(classes[i], pt[i])) {
                            bool = false;
                            break;
                        }
                    }
                    if (bool) {
                        c = declaredConstructor;
                        break;
                    }
                }
            }
        }
        return c;
    }

    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, Object... args) {
        return findMethod(clazz, name, Arrays.stream(args).toList());
    }

    public static @Nullable <T> Method findMethod(@NotNull Class<T> clazz, String name, List<Object> args) {
        Method m = null;

        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length == 1) {
            m = methods[0];
        } else {
            Class<?>[] classes = args.stream().map(Object::getClass).toArray(Class[]::new);

            try {
                m = clazz.getDeclaredMethod(name, classes);
            } catch (Throwable e) {
                for (Method method : methods) {
                    if (!method.getName().equals(name)) continue;
                    if (method.getParameterCount() != args.size()) continue;

                    boolean bool = true;
                    Class<?>[] pt = method.getParameterTypes();
                    for (int i = 0; i < method.getParameterCount(); i++) {
                        if (!ClassUtils.isAssignable(classes[i], pt[i])) {
                            bool = false;
                            break;
                        }
                    }
                    if (bool) {
                        m = method;
                        break;
                    }
                }
            }
        }
        return m;
    }

    /**
     * Attempts to set a constructor as accessible.
     *
     * <p>
     * This method uses the vanilla `Constructor.setAccessible(true)` method, but falls back to using `Unsafe.putBoolean(i, true)` in case reflection fails.
     * </p>
     *
     * @param constructor the constructor to set as accessible
     */
    public static <T> Constructor<T> setAccessible(Constructor<T> constructor) {
        MakeSure.notNull(constructor, "Tried to setAccessible a null constructor");
        try {
            constructor.setAccessible(true);
        } catch (Exception e) {
            int i = getOverrideOffset();
            UnsafeAccess.getUnsafe().putBoolean(constructor, i, true);
        }
        return constructor;
    }

    /**
     * Attempts to set a method as accessible.
     *
     * <p>
     * This method uses the vanilla `Method.setAccessible(true)` method, but falls back to using `Unsafe.putBoolean(i, true)` in case reflection fails.
     * </p>
     *
     * @param method the method to set as accessible
     */
    public static Method setAccessible(Method method) {
        MakeSure.notNull(method, "Tried to setAccessible a null method");
        try {
            method.setAccessible(true);
        } catch (Exception e) {
            int i = getOverrideOffset();
            UnsafeAccess.getUnsafe().putBoolean(method, i, true);
        }
        return method;
    }

    /**
     * Attempts to set a field as accessible.
     *
     * <p>
     * This method uses the vanilla `Field.setAccessible(true)` method, but falls back to using `Unsafe.putBoolean(i, true)` in case reflection fails.
     * </p>
     *
     * @param field the field to set as accessible
     */
    public static Field setAccessible(Field field) {
        MakeSure.notNull(field, "Tried to setAccessible a null field");
        try {
            field.setAccessible(true);
        } catch (Exception e) {
            int i = getOverrideOffset();
            UnsafeAccess.getUnsafe().putBoolean(field, i, true);
        }
        return field;
    }

    public static Field tryRemoveFinal(Field f) {
        MakeSure.notNull(f, "Tried to remove final from a null field");

        if (Modifier.isFinal(f.getModifiers())) {
            Unsafe unsafe = UnsafeAccess.getUnsafe();
            long offset;

            try {
                offset = UnsafeAccess.getObjectFieldOffset(Field.class, "modifiers");
            } catch (Exception e) {
                for (offset = 0; ; offset++) {
                    if (unsafe.getInt(f, offset) == f.getModifiers()) {
                        break;
                    }
                }
            }

            int modifiers = unsafe.getInt(f, offset);

            if (f.getModifiers() == modifiers) {
                unsafe.putInt(f, offset, f.getModifiers() & -17);
            } else {
                throw new UnsupportedOperationException("couldn't remove final");
            }
        }
        return f;
    }

    private static Method addOpensOrExports;

    public static void addOpensOrExports(Module module, String pn, Module other, boolean open, boolean syncVM) {
        if (addOpensOrExports == null) {
            try {
                addOpensOrExports = ReflectionUtil.setAccessible(ReflectionUtil.class.getModule().getClass().getDeclaredMethod("implAddExportsOrOpens", String.class, Module.class, boolean.class, boolean.class));
            } catch (NoSuchMethodException e) {
                DarkMatterLog.error("Couldn't add new {}. Expect errors", open ? "opens" : "exports");
                return;
            }
        }
        try {
            addOpensOrExports.invoke(module, pn, other, open, syncVM);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    //https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass
    public static int getOverrideOffset() {
        if (offset == -1) {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe"), f1 = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                f1.setAccessible(false);
                Unsafe unsafe = (Unsafe) f.get(null);
                int i;//override boolean byte offset. should result in 12 for java 17
                for (i = 0; unsafe.getBoolean(f, i) == unsafe.getBoolean(f1, i); i++) ;
                offset = i;
            } catch (Exception ignored) {
                offset = 12; //fallback to 12 just in case
            }
        }
        MakeSure.isFalse(offset == -1);
        return offset;
    }

    private static Constructor<?> handlesMockConstructor;

    /**
     * Creates a mock {@link MethodHandles.Lookup} for the given class.
     *
     * @param clazz the class for which to create a mock {@link MethodHandles.Lookup}
     * @return a mock {@link MethodHandles.Lookup} for the given class
     * @throws RuntimeException if an error occurs while creating the mock lookup class
     */
    public static @NotNull MethodHandles.Lookup mockLookupClass(Class<?> clazz) {
        try {
            if (handlesMockConstructor == null) {
                Constructor<?> c = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
                handlesMockConstructor = ReflectionUtil.setAccessible(c);
            }
            return ((MethodHandles.Lookup) handlesMockConstructor.newInstance(clazz));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method forName0;

    /**
     * Attempts to access a restricted class with the given name.
     *
     * @param name the name of the class to access
     * @return the Class object for the class with the given name
     * @throws RuntimeException if the class cannot be accessed or if an error occurs while accessing it
     */
    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) {
        if (forName0 == null) {
            try {
                Method m = Class.class.getDeclaredMethod("forName0", String.class, boolean.class, ClassLoader.class, Class.class);
                forName0 = ReflectionUtil.setAccessible(m);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (Class<?>) forName0.invoke(null, name, false, loader, Class.class);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> accessRestrictedClass(String name) {
        return accessRestrictedClass(name, null);
    }

    public static Field getField(Class<?> clazz, String name) {
        return getField(clazz, name, false);
    }

    public static Field getField(Class<?> clazz, String name, boolean accessible) {
        try {
            var f = clazz.getDeclaredField(name);
            return accessible ? setAccessible(f) : f;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getField(Field field, Object o) {
        try {
            return setAccessible(field).get(o);
        } catch (IllegalAccessException e) {
            return UnsafeAccess.getObject(field, o);
        }
    }

    public static void setField(Field field, Object o, Object value) {
        try {
            ReflectionUtil.tryRemoveFinal(setAccessible(field)).set(o, value);
        } catch (IllegalAccessException e) {
            UnsafeAccess.putObject(field, o, value);
        }
    }
}
