package me.melontini.dark_matter.impl.base.reflect;

import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.reflect.UnsafeAccess;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.*;
import java.util.List;

/**
 * @author <a href="https://stackoverflow.com/questions/55918972/unable-to-find-method-sun-misc-unsafe-defineclass">source</a>
 */
@ApiStatus.Internal
public class ReflectionInternals {
    private ReflectionInternals() {
        throw new UnsupportedOperationException();
    }

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

    public static <T> Field findField(Class<T> clazz, String name) {
        Field f = null;

        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 1) {
            f = fields[0];
        } else {
            for (Field field : fields) {
                if (field.getName().equals(name)) {
                    f = field;
                    break;
                }
            }
        }
        return f;
    }

    private static VarHandle override;

    public static <T extends AccessibleObject> T setAccessible(T member, boolean set) {
        MakeSure.notNull(member, "Tried to setAccessible a null constructor");
        try {
            member.setAccessible(set);
        } catch (Exception e) {
            try {
                if (override == null) {
                    MethodHandles.Lookup lookup = stealTrustedLookup();
                    override = lookup.findVarHandle(AccessibleObject.class, "override", boolean.class);
                }
                override.set(member, set);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return member;
    }

    private static VarHandle modifiers;
    private static MethodHandle setFieldAccessor;

    public static Field tryRemoveFinal(Field f) {
        MakeSure.notNull(f, "Tried to remove final from a null field");

        if (Modifier.isFinal(f.getModifiers())) {
            try {
                if (modifiers == null) {
                    modifiers = stealTrustedLookup().findVarHandle(Field.class, "modifiers", int.class);
                }
                modifiers.set(f, ((int) modifiers.get(f)) & ~Modifier.FINAL);
                if (setFieldAccessor == null) {
                    Class<?> cls = Class.forName("jdk.internal.reflect.FieldAccessor");
                    setFieldAccessor = stealTrustedLookup().findVirtual(Field.class, "setFieldAccessor", MethodType.methodType(void.class, cls, boolean.class));
                }
                setFieldAccessor.invokeWithArguments(f, null, false);
                setFieldAccessor.invokeWithArguments(f, null, true);
            } catch (Throwable e) {
                DarkMatterLog.error("Couldn't remove final from field " + f.getName(), e);
            }
        }
        return f;
    }

    private static MethodHandle addOpensOrExports;

    @Deprecated
    public static void addOpensOrExports(Module module, String pn, Module other, boolean open, boolean syncVM) {
        if (addOpensOrExports == null) {
            try {
                MethodHandles.Lookup lookup = stealTrustedLookup();
                addOpensOrExports = lookup.findVirtual(Module.class, "implAddExportsOrOpens", MethodType.methodType(void.class, String.class, Module.class, boolean.class, boolean.class));
            } catch (IllegalAccessException | NoSuchMethodException e) {
                DarkMatterLog.error("Couldn't add new {}. Expect errors", open ? "opens" : "exports");
                return;
            }
        }
        try {
            addOpensOrExports.invokeWithArguments(module, pn, other, open, syncVM);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandles.Lookup trustedLookup;

    public static @NotNull MethodHandles.Lookup mockLookupClass(Class<?> clazz) {
        return stealTrustedLookup().in(clazz);
    }

    public static MethodHandles.Lookup stealTrustedLookup() {
        try {
            if (trustedLookup == null) {
                Field f = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                trustedLookup = (MethodHandles.Lookup) UnsafeAccess.getReference(f, null);
            }
            return trustedLookup;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandle forName0;

    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) {
        try {
            if (forName0 == null) {
                forName0 = stealTrustedLookup()
                        .findStatic(Class.class, "forName0", MethodType.methodType(Class.class, String.class, boolean.class, ClassLoader.class, Class.class));
            }
            return (Class<?>) forName0.invokeWithArguments(null, name, false, loader, Class.class);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getField(Class<?> clazz, String name, boolean accessible) {
        try {
            return Reflect.findField(clazz, name).map(field -> accessible ? setAccessible(field, true) : field)
                    .orElseThrow(() -> new NoSuchFieldException(name));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getField(Field field, Object o) {
        try {
            return stealTrustedLookup().unreflectGetter(field).invoke(o);
        } catch (Throwable e) {
            return UnsafeAccess.getReference(field, o);
        }
    }

    public static void setField(Field field, Object o, Object value) {
        try {
            ReflectionInternals.tryRemoveFinal(setAccessible(field, true)).set(o, value);
        } catch (IllegalAccessException e) {
            UnsafeAccess.putReference(field, o, value);
        }
    }
}
