package me.melontini.dark_matter.api.base.reflect.wrappers;

import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.Exceptions;

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
                return (R) Exceptions.supply(() -> method.invoke(obj, args));
            }

            @Override
            public GenericMethod<O, R> accessible(boolean accessible) {
                method.setAccessible(accessible);
                return this;
            }
        };
    }
}
