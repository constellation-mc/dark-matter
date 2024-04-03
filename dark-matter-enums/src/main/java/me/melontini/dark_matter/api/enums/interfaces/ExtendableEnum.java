package me.melontini.dark_matter.api.enums.interfaces;

import me.melontini.dark_matter.api.enums.EnumUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public interface ExtendableEnum<T extends Enum<T>, C extends Supplier<Object[]>> {
    /**
     * Extends an enum with a new element.
     *
     * <p>
     * This method allows adding new elements to an enum at runtime. It should only be used if absolutely necessary, as extending enums can cause unpredictable behavior and can break code that relies on a fixed set of enum values.
     * In particular, switch statements and maps or lists that use enums as keys or values may fail when new elements are added.
     * </p>
     *
     * @param internalName The internal name of the new enum element. This name is used by the {@link java.lang.Enum#valueOf(Class, String)} method to map from a string representation of the enum to its corresponding enum constant.
     * Note that some enums may provide their own names (e.g {@link net.minecraft.util.Formatting}), which are different from the internal names. Should be uppercase with no special symbols.
     * @param params the parameters to pass to the constructor of the new enum element
     * @return the newly created enum element
     * @throws IllegalStateException if the `extend` method is not implemented by the enum class
     */
    default T dm$extend(String internalName, C params) {
        try {
            return EnumUtils.callEnumInvoker(((T) this).getDeclaringClass(), internalName, params.get());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static <C extends Supplier<Object[]>, T extends Enum<T> & ExtendableEnum<T, C>> T extend(Class<T> cls, String internalName, C params) {
        try {
            return EnumUtils.callEnumInvoker(cls, internalName, params.get());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @ApiStatus.Internal
    default void dark_matter$init(Object... args) {}
}
