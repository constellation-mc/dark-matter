package me.melontini.dark_matter.api.base.reflect.wrappers;

import me.melontini.dark_matter.api.base.reflect.MiscReflection;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.Utilities;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public interface GenericField<O, T> {
    T get(O obj);
    void set(O obj, T value);
    GenericField<O, T> accessible(boolean accessible);

    static <O, T> GenericField<O, T> of(Class<O> cls, String name) {
        return (GenericField<O, T>) Reflect.findField(cls, name).map(GenericField::of)
                .orElseThrow(() -> new IllegalStateException("No such field %s.%s!".formatted(cls, name)));
    }

    static <O, T> GenericField<O, T> of(Field field) {
        return new GenericField<>() {
            @Override
            public T get(O obj) {
                return (T) Utilities.supplyUnchecked(() -> field.get(obj));
            }

            @Override
            public void set(O obj, T value) {
                Utilities.runUnchecked(() -> field.set(obj, value));
            }

            @Override
            public GenericField<O, T> accessible(boolean accessible) {
                field.setAccessible(accessible);
                return this;
            }
        };
    }

    static <O, T> GenericField<O, T> ofTrusted(Class<O> cls, String name) {
        return  (GenericField<O, T>) Reflect.findField(cls, name).map(GenericField::ofTrusted)
                .orElseThrow(() -> new IllegalStateException("No such field %s.%s!".formatted(cls, name)));
    }

    static <O, T> GenericField<O, T> ofTrusted(Field field) {
        VarHandle handle = Utilities.supplyUnchecked(() -> MiscReflection.unreflectVarHandle(field));
        return new GenericField<>() {
            @Override
            public T get(O obj) {
                if (obj == null) return (T) Utilities.supplyUnchecked(handle::get);
                else return (T) Utilities.supplyUnchecked(() -> handle.get(obj));
            }

            @Override
            public void set(O obj, T value) {
                if (obj == null) Utilities.runUnchecked(() -> handle.set(value));
                else Utilities.runUnchecked(() -> handle.set(obj, value));
            }

            @Override
            public GenericField<O, T> accessible(boolean accessible) {
                return this;
            }
        };
    }
}
