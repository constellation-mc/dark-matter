package me.melontini.dark_matter.api.base.reflect.wrappers;

import me.melontini.dark_matter.api.base.reflect.MiscReflection;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.Utilities;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public interface GenericConstructor<O> {
    O construct(Object... args);
    GenericConstructor<O> accessible(boolean accessible);

    static <O> GenericConstructor<O> of(Class<O> cls, Class<?>... args) {
        return Reflect.findConstructor(cls, args).map(GenericConstructor::of)
                .orElseThrow(() -> new IllegalStateException("No such constructor %s(%s)!".formatted(cls, Arrays.toString(args))));
    }

    static <O> GenericConstructor<O> of(Constructor<O> ctx) {
        return new GenericConstructor<>() {
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

    static <O> GenericConstructor<O> ofTrusted(Class<O> cls, Class<?>... args) {
        return Reflect.findConstructor(cls, args).map(GenericConstructor::ofTrusted)
                .orElseThrow(() -> new IllegalStateException("No such constructor %s(%s)!".formatted(cls, Arrays.toString(args))));
    }

    static <O> GenericConstructor<O> ofTrusted(Constructor<O> ctx) {
        MethodHandle handle = Utilities.supplyUnchecked(() -> MiscReflection.lookupIn(ctx.getDeclaringClass()).unreflectConstructor(ctx));
        return new GenericConstructor<>() {
            @Override
            public O construct(Object... args) {
                return (O) Utilities.supplyUnchecked(() -> handle.invoke(args));
            }

            @Override
            public GenericConstructor<O> accessible(boolean accessible) {
                return this;
            }
        };
    }
}
