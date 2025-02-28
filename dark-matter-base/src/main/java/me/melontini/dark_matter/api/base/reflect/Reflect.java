package me.melontini.dark_matter.api.base.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.base.reflect.ReflectionInternals;
import org.jetbrains.annotations.NotNull;

@UtilityClass
@SuppressWarnings("unused")
public class Reflect {

  //
  // find members
  //

  public static <T> Optional<Constructor<T>> findConstructor(
      @NotNull Class<T> clazz, Object... args) {
    return Optional.ofNullable(ReflectionInternals.findConstructor(
        clazz, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new)));
  }

  public static <T> Optional<Constructor<T>> findConstructor(
      @NotNull Class<T> clazz, Class<?>... args) {
    return Optional.ofNullable(ReflectionInternals.findConstructor(clazz, args));
  }

  public static Optional<Method> findMethod(@NotNull Class<?> clazz, String name, Object... args) {
    return Optional.ofNullable(ReflectionInternals.findMethod(
        clazz, false, name, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new)));
  }

  public static Optional<Method> findMethod(
      @NotNull Class<?> clazz, String name, Class<?>... args) {
    return Optional.ofNullable(ReflectionInternals.findMethod(clazz, false, name, args));
  }

  public static Optional<Field> findField(@NotNull Class<?> clazz, String name) {
    return Optional.ofNullable(ReflectionInternals.findField(clazz, false, name));
  }

  public static Optional<Method> findMethodInHierarchy(
      @NotNull Class<?> clazz, String name, Object... args) {
    return Optional.ofNullable(ReflectionInternals.findMethod(
        clazz, true, name, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new)));
  }

  public static Optional<Method> findMethodInHierarchy(
      @NotNull Class<?> clazz, String name, Class<?>... args) {
    return Optional.ofNullable(ReflectionInternals.findMethod(clazz, true, name, args));
  }

  public static Optional<Field> findFieldInHierarchy(@NotNull Class<?> clazz, String name) {
    return Optional.ofNullable(ReflectionInternals.findField(clazz, true, name));
  }

  //
  // set accessible
  //

  public static <T extends AccessibleObject> T setAccessible(T member) {
    return ReflectionInternals.setAccessible(member, true);
  }

  public static <T extends AccessibleObject> T setAccessible(T member, boolean flag) {
    return ReflectionInternals.setAccessible(member, flag);
  }
}
