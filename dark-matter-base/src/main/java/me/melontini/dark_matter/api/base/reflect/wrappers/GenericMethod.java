package me.melontini.dark_matter.api.base.reflect.wrappers;

import me.melontini.dark_matter.api.base.reflect.MiscReflection;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.Utilities;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public interface GenericMethod<O, R> {
    R invoke(O obj, Object... args);
    GenericMethod<O, R> accessible(boolean accessible);

    static <O, R> GenericMethod<O, R> of(Class<O> cls, String name, Class<?>... args) {
        return (GenericMethod<O, R>) Reflect.findMethod(cls, name, args).map(GenericMethod::of)
                .orElseThrow(() -> new IllegalStateException("No such method %s.%s(%s)!".formatted(cls, name, Arrays.toString(args))));
    }

    static <O, R> GenericMethod<O, R> of(Method method) {
        return new GenericMethod<>() {
            @Override
            public R invoke(O obj, Object... args) {
                return (R) Utilities.supplyUnchecked(() -> method.invoke(obj, args));
            }

            @Override
            public GenericMethod<O, R> accessible(boolean accessible) {
                method.setAccessible(accessible);
                return this;
            }
        };
    }

    static <O, R> GenericMethod<O, R> ofTrusted(Class<O> cls, String name, Class<?>... args) {
        return (GenericMethod<O, R>) Reflect.findMethod(cls, name, args).map(GenericMethod::ofTrusted)
                .orElseThrow(() -> new IllegalStateException("No such method %s.%s(%s)!".formatted(cls, name, Arrays.toString(args))));
    }

    static <O, R> GenericMethod<O, R> ofTrusted(Method method) {
        MethodHandle handle = Utilities.supplyUnchecked(() -> MiscReflection.lookupIn(method.getDeclaringClass()).unreflect(method));
        return new GenericMethod<>() {
            @Override
            public R invoke(O obj, Object... args) {
                if (obj == null) return (R) Utilities.supplyUnchecked(() -> handle.invoke(args));
                return (R) Utilities.supplyUnchecked(() -> handle.invoke(obj, args));
            }

            @Override
            public GenericMethod<O, R> accessible(boolean accessible) {
                return this;
            }
        };
    }
}
