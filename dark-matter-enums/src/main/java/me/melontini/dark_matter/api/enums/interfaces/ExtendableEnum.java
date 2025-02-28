package me.melontini.dark_matter.api.enums.interfaces;

import java.util.function.Supplier;
import me.melontini.dark_matter.api.enums.Parameters;
import me.melontini.dark_matter.impl.enums.EnumInternals;
import org.jetbrains.annotations.ApiStatus;

/**
 * Enums marked with this interface support extension using {@link ExtendableEnum#extend(Class, String, Supplier)}.
 *
 * <p>
 * This should only be used if absolutely necessary, as extending enums can cause unpredictable behavior and can break code that relies on a fixed set of enum values.
 * In particular, switch statements and maps or lists that use enums as keys or values may fail when new elements are added.
 * </p>
 */
public interface ExtendableEnum<C extends Supplier<Object[]>> {

  static <T extends Enum<T> & ExtendableEnum<C>, C extends Supplier<Object[]>> T extend(
      Class<T> cls, String internalName, C params) {
    return EnumInternals.extend(cls, internalName, params);
  }

  static <T extends Enum<T> & ExtendableEnum<Parameters.Empty>> T extend(
      Class<T> cls, String internalName) {
    return EnumInternals.extend(cls, internalName, Parameters.EMPTY);
  }

  @ApiStatus.Internal
  default void dark_matter$init(C args) {}
}
