package me.melontini.dark_matter.api.base.util.classes;

@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);


    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        return (l, r, s) -> {
            accept(l, r, s);
            after.accept(l, r, s);
        };
    }
}
