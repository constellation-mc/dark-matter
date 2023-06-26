package me.melontini.dark_matter.enums.util;

import me.melontini.dark_matter.DarkMatterLog;
import me.melontini.dark_matter.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.reflect.ReflectionUtil;
import me.melontini.dark_matter.util.MakeSure;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class EnumUtils {
    private EnumUtils() {
        throw new UnsupportedOperationException();
    }
    private static final Map<Class<?>, Field> ENUM_TO_FIELD = new HashMap<>();//Store the field in case someone tries to call this method a bunch of times

    /**
     * Attempts to extend an enum by reflecting on its internal fields and adding a new enum constant.
     *
     * <p>
     * This method allows adding new elements to an enum at runtime. It should only be used if absolutely necessary, as extending enums can cause unpredictable behavior and can break code that relies on a fixed set of enum values.
     * In particular, switch statements and maps or lists that use enums as keys or values may fail when new elements are added.
     * </p>
     * <p>
     * This can also break in future java versions.
     *
     * @param enumClass    the class of the enum to extend
     * @param internalName The internal name of the new enum element. This name is used by the {@link java.lang.Enum#valueOf(Class, String)} method to map from a string representation of the enum to its corresponding enum constant.
     *                     Note that some enums may provide their own names (e.g {@link net.minecraft.util.Formatting}), which are different from the internal names.
     * @param params       the parameters to pass to the constructor of the new enum element
     * @return the newly created enum element
     * @throws RuntimeException if an error occurs during the extension process.
     */
    /*probably a good idea to make this synchronized*/
    public static synchronized <T extends Enum<?>> T extendByReflecting(boolean reflectOnly, Class<T> enumClass, String internalName, Object... params) {
        if (!reflectOnly && ExtendableEnum.class.isAssignableFrom(enumClass)) {
            try {
                return callEnumInvoker(enumClass, internalName, params);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Class<?> enumArrayClass = enumClass.arrayType();

            Field enumArray = ENUM_TO_FIELD.getOrDefault(enumClass, null); //we can't just request $VALUES directly because the field uses internal field_[some number] name format.
            if (enumArray == null)
                for (Field field : enumClass.getDeclaredFields()) {
                    if (Modifier.isPrivate(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && field.isSynthetic() && field.getType() == enumArrayClass) {
                        enumArray = field;
                        break;
                    }
                }
            MakeSure.notNull(enumArray, "(reflection) couldn't find enum's $VALUES");
            ENUM_TO_FIELD.putIfAbsent(enumClass, enumArray);
            try {
                enumClass.getMethod("values").invoke(enumClass);//we need to init enumClass to access its fields, duh.
            } catch (Exception ignored) {
                throw new IllegalStateException("(reflection) couldn't find values() in an enum");
            }

            //this list seems redundant, can't I just use an array?
            List<T> entries = new ArrayList<>(Arrays.asList((T[]) ReflectionUtil.getField(enumArray, enumClass)));
            T last = entries.get(entries.size() - 1);

            List<Object> list = new ArrayList<>(Arrays.asList(internalName, last.ordinal() + 1));
            list.addAll(Arrays.stream(params).toList());

            T entry;
            try {
                Constructor<T> c = ReflectionUtil.findConstructor(enumClass, list);
                MakeSure.notNull(c, "(reflection) Couldn't find enum constructor, possible parameter mismatch?");
                entry = (T) MethodHandles.lookup().unreflectConstructor(ReflectionUtil.setAccessible(c)).invokeWithArguments(list);//thankfully, for some reason MethodHandles can invoke enum constructors.
            } catch (Exception e) {
                throw new ReflectiveOperationException("(reflection) Couldn't create new enum instance", e);
            }
            MakeSure.notNull(entry, "(reflection) Couldn't create new enum instance");
            T[] tempArray = ArrayUtils.add((T[]) ReflectionUtil.getField(enumArray, enumClass), entry);

            ReflectionUtil.setField(enumArray, enumClass, tempArray);//We assume that $VALUES are always private and final. Although this isn't always true.
            clearEnumCache(enumClass);
            return entry;
        } catch (Throwable e) {
            throw new RuntimeException("(reflection) Enum not extended", e);
        }
    }

    public static synchronized <T extends Enum<?>> T extendByReflecting(Class<T> enumClass, String internalName, Object... params) {
        return extendByReflecting(false, enumClass, internalName, params);
    }

    /**
     * Attempts to clear the internal cache of enum constants for the given enum class.
     *
     * @param cls the class of the enum for which to clear the cache
     */
    public static synchronized void clearEnumCache(Class<? extends Enum<?>> cls) {
        try {
            ReflectionUtil.setField(Class.class.getDeclaredField("enumConstants"), cls, null);
        } catch (Exception e) {
            DarkMatterLog.error("Couldn't clear enumConstants. This shouldn't really happen", e);
        }

        try {
            ReflectionUtil.setField(Class.class.getDeclaredField("enumConstantDirectory"), cls, null);
        } catch (Exception e) {
            DarkMatterLog.error("Couldn't clear enumConstantDirectory. This shouldn't really happen", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T callEnumInvoker(Class<T> cls, String internalName, Object... params) throws Throwable {
        MakeSure.notEmpty(internalName, String.format("Tried to extend %s with an empty name", cls.getSimpleName()));
        List<Object> list = new ArrayList<>(List.of(internalName));
        list.addAll(List.of(params));

        return (T) ReflectionUtil.setAccessible(ReflectionUtil.findMethod(cls, "dark_matter$extendEnum", list))
                .invoke(cls, list.toArray());
    }
}
