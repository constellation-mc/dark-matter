package me.melontini.dark_matter.api.base.reflect.wrappers;

import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.Utilities;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public interface GenericConstructor<O> {
    O construct(Object... args);
    GenericConstructor<O> accessible(boolean accessible);

    static <O> GenericConstructor<O> of(Class<O> cls, Class<?>... args) {
        return Reflect.findConstructor(cls, args).map(GenericConstructor::of)
                .orElseThrow(() -> new IllegalStateException("No such constructor %s(%s)!".formatted(cls, Arrays.toString(args))));
    }

    static <O> GenericConstructor<O> of(Constructor<O> ctx) {
        return new GenericConstructor<O>() {
            @Override
            public O construct(Object... args) {
                return Utilities.supplyUnchecked(() -> ctx.newInstance(args));
            }

            @Override
            public GenericConstructor<O> accessible(boolean accessible) {
                ctx.setAccessible(accessible);
                return this;
            }
        };
    }
}