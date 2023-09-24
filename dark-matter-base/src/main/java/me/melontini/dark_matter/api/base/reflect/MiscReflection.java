package me.melontini.dark_matter.api.base.reflect;

import me.melontini.dark_matter.impl.base.reflect.MiscReflectionInternals;
import me.melontini.dark_matter.impl.base.reflect.ReflectionInternals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MiscReflection {

    public static Function<Object, Object> createGetter(Field field, MethodHandles.Lookup lookup) {
        return MiscReflectionInternals.createGetter(field, lookup);
    }

    public static Supplier<Object> createStaticGetter(Field field, MethodHandles.Lookup lookup) {
        return MiscReflectionInternals.createStaticGetter(field, lookup);
    }

    public static BiConsumer<Object, Object> createSetter(Field field, MethodHandles.Lookup lookup) {
        return MiscReflectionInternals.createSetter(field, lookup);
    }

    public static Consumer<Object> createStaticSetter(Field field, MethodHandles.Lookup lookup) {
        return MiscReflectionInternals.createStaticSetter(field, lookup);
    }

    //
    // no
    //

    public static Field tryRemoveFinal(Field field) {
        return ReflectionInternals.tryRemoveFinal(field);
    }

    public static @NotNull MethodHandles.Lookup mockLookupClass(Class<?> clazz) {
        return ReflectionInternals.mockLookupClass(clazz);
    }

    public static Class<?> accessRestrictedClass(String name, @Nullable ClassLoader loader) {
        return ReflectionInternals.accessRestrictedClass(name, loader);
    }

    public static Class<?> accessRestrictedClass(String name) {
        return ReflectionInternals.accessRestrictedClass(name, null);
    }
}
