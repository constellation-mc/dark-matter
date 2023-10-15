package me.melontini.dark_matter.api.base.util.classes;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
    T get() throws E;
}
