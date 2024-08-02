package me.melontini.dark_matter.api.base.reflect.wrappers;

import java.lang.reflect.Field;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.util.Exceptions;

@SuppressWarnings("unchecked")
public interface GenericField<O, T> {
  T get(O obj);

  void set(O obj, T value);

  GenericField<O, T> accessible(boolean accessible);

  static <O, T> GenericField<O, T> of(Class<O> cls, String name) {
    return (GenericField<O, T>) Reflect.findField(cls, name)
        .map(GenericField::of)
        .orElseThrow(() -> new IllegalStateException("No such field %s.%s!".formatted(cls, name)));
  }

  static <O, T> GenericField<O, T> of(Field field) {
    return new GenericField<>() {
      @Override
      public T get(O obj) {
        return (T) Exceptions.supply(() -> field.get(obj));
      }

      @Override
      public void set(O obj, T value) {
        Exceptions.run(() -> field.set(obj, value));
      }

      @Override
      public GenericField<O, T> accessible(boolean accessible) {
        field.setAccessible(accessible);
        return this;
      }
    };
  }
}
