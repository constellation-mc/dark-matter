package me.melontini.dark_matter.impl.enums;

import static me.melontini.dark_matter.api.base.util.Utilities.cast;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.Synchronized;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.reflect.UnsafeUtils;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.enums.interfaces.ExtendableEnum;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;

public class EnumInternals {

  private static final Map<Class<?>, Field> ENUM_TO_FIELD =
      new HashMap<>(); // Store the field in case someone tries to call this method a bunch of times
  private static final Map<Class<?>, Map<String, Enum<?>>> CACHE = new HashMap<>();

  public static <T extends Enum<T>> T getEnumConstant(String name, Class<T> cls) {
    var directory = CACHE.get(cls);
    if (directory == null) {
      synchronized (CACHE) {
        directory = new HashMap<>();
        CACHE.put(cls, directory);
      }
    }

    T cnst = (T) directory.get(name);
    if (cnst == null) {
      synchronized (CACHE) {
        directory.clear();

        T[] entries = getValues(cls);
        for (T entry : entries) {
          directory.put(entry.name(), entry);
        }
        return (T) directory.get(name);
      }
    }
    return cnst;
  }

  private static <T extends Enum<T>> T[] getValues(Class<T> cls) {
    try {
      Method method = cls.getMethod("values");
      return (T[]) method.invoke(cls);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(
          "Enum class %s does not follow the enum spec.".formatted(cls.getName()), e);
    }
  }

  /*probably a good idea to make this synchronized*/
  public static synchronized <T extends Enum<?>> T extendByReflecting(
      boolean reflectOnly, Class<T> enumClass, String internalName, Object... params) {
    if (!reflectOnly && ExtendableEnum.class.isAssignableFrom(enumClass)) {
      try {
        return callEnumInvoker(enumClass, internalName, params);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    }

    try {
      Class<?> enumArrayClass = enumClass.arrayType();

      Field enumValues = ENUM_TO_FIELD.getOrDefault(
          enumClass,
          null); // we can't just request $VALUES directly because the field uses internal
      // field_[some number] name format.
      if (enumValues == null) {
        int mod = Modifier.PRIVATE | Modifier.STATIC | Opcodes.ACC_SYNTHETIC;
        enumValues = Arrays.stream(enumClass.getDeclaredFields())
            .filter(
                field -> (field.getModifiers() & mod) == mod && field.getType() == enumArrayClass)
            .findFirst()
            .orElseThrow();
      }
      ENUM_TO_FIELD.putIfAbsent(
          enumClass,
          Objects.requireNonNull(enumValues, "(reflection) couldn't find enum's $VALUES"));
      enumClass
          .getMethod("values")
          .invoke(enumClass); // we need to init enumClass to access its fields, duh.

      T[] entries = cast(UnsafeUtils.getReference(
          enumValues, enumClass)); // ReflectionUtil.getField(enumValues, enumClass)
      T last = entries[entries.length - 1];

      Object[] list = ArrayUtils.addAll(new Object[] {internalName, last.ordinal() + 1}, params);

      T entry;
      try {
        Constructor<T> c = Reflect.findConstructor(enumClass, list)
            .orElseThrow(() -> new NullPointerException(
                "(reflection) Couldn't find enum constructor, possible parameter mismatch?"));
        entry = cast(MethodHandles.lookup()
            .unreflectConstructor(Reflect.setAccessible(c))
            .invokeWithArguments(
                list)); // thankfully, for some reason MethodHandles can invoke enum constructors.
      } catch (Exception e) {
        throw new ReflectiveOperationException("(reflection) Couldn't create new enum instance", e);
      }
      Objects.requireNonNull(entry, "(reflection) Couldn't create new enum instance");
      T[] tempArray = ArrayUtils.add(entries, entry);

      UnsafeUtils.putReference(enumValues, enumClass, tempArray);
      clearEnumCache(enumClass);
      return entry;
    } catch (Throwable e) {
      throw new RuntimeException("(reflection) Enum not extended", e);
    }
  }

  @Synchronized
  public static <C extends Supplier<Object[]>, T extends Enum<T> & ExtendableEnum<C>> @NotNull T extend(@NonNull Class<T> cls, @NonNull String internalName, @NonNull C params) {
    try {
      T r = callEnumInvoker(cls, internalName, params.get());
      r.dark_matter$init(params);
      return r;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static synchronized void clearEnumCache(Class<? extends Enum<?>> cls) {
    try {
      Reflect.findField(Class.class, "enumConstants")
          .ifPresent(field -> UnsafeUtils.putReference(field, cls, null));
    } catch (Exception e) {
      DarkMatterLog.error("Couldn't clear enumConstants. This shouldn't really happen", e);
    }

    try {
      Reflect.findField(Class.class, "enumConstantDirectory")
          .ifPresent(field -> UnsafeUtils.putReference(field, cls, null));
    } catch (Exception e) {
      DarkMatterLog.error("Couldn't clear enumConstantDirectory. This shouldn't really happen", e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends Enum<?>> T callEnumInvoker(
      Class<T> cls, String internalName, Object... params) throws Throwable {
    MakeSure.notEmpty(
        internalName, String.format("Tried to extend %s with an empty name", cls.getSimpleName()));
    List<Object> list = new ArrayList<>(List.of(internalName));
    list.addAll(List.of(params));
    params = list.toArray();

    return (T) Reflect.findMethod(cls, "dark_matter$extendEnum", params)
        .orElseThrow(() -> new IllegalStateException(
            "%s doesn't have a dark_matter$extendEnum method".formatted(cls.getName())))
        .invoke(cls, params);
  }
}
