package me.melontini.dark_matter.api.base.util.classes;

@FunctionalInterface
public interface TriPredicate<T, U, V> {
    boolean test(T t, U u, V v);


    default TriPredicate<T, U, V> and(TriPredicate<? super T, ? super U, ? super V> other) {
        return (t, u, v) -> test(t, u, v) && other.test(t, u, v);
    }

    default TriPredicate<T, U, V> negate() {
        return (t, u, v) -> !test(t, u, v);
    }

    default TriPredicate<T, U, V> or(TriPredicate<? super T, ? super U, ? super V> other) {
        return (t, u, v) -> test(t, u, v) || other.test(t, u, v);
    }
}
