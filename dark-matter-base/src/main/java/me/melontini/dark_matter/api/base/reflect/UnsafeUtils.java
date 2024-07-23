package me.melontini.dark_matter.api.base.reflect;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.base.reflect.UnsafeInternals;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class UnsafeUtils {

  public static MethodHandles.Lookup lookupIn(Class<?> cls) {
    return UnsafeInternals.lookupIn(cls);
  }

  public static Class<?> defineClass(
      ClassLoader loader, String name, byte[] bytes, @Nullable ProtectionDomain domain) {
    return UnsafeInternals.defineClass(loader, name, bytes, domain);
  }

  public static void putReference(Field field, Object o, Object value) {
    UnsafeInternals.setReference(field, o, value);
  }

  public static <T> T getReference(Field field, @Nullable Object o) {
    return UnsafeInternals.getReference(field, o);
  }

  public static <T> T allocateInstance(Class<T> cls) throws InstantiationException {
    return UnsafeInternals.allocateInstance(cls);
  }
}
