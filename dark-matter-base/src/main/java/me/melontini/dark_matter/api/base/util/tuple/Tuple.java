package me.melontini.dark_matter.api.base.util.tuple;

import java.util.function.Function;

public interface Tuple<L, R> {

  static <L, R> Tuple<L, R> of(L left, R right) {
    return new ImmutableTuple<>(left, right);
  }

  static <L, R> Tuple<L, R> mutable(L left, R right) {
    return new MutableTuple<>(left, right);
  }

  L left();

  R right();

  Tuple<L, R> left(L left);

  Tuple<L, R> right(R right);

  <L1, R1> Tuple<L1, R1> fork(L1 left, R1 right);

  default boolean isLeftPresent() {
    return left() != null;
  }

  default boolean isRightPresent() {
    return right() != null;
  }

  default Tuple<R, L> swap() {
    return fork(right(), left());
  }

  default <V> Tuple<V, R> mapLeft(Function<? super L, ? extends V> mapper) {
    return fork(mapper.apply(left()), right());
  }

  default <V> Tuple<L, V> mapRight(Function<? super R, ? extends V> mapper) {
    return fork(left(), mapper.apply(right()));
  }

  default <V, V1> Tuple<V, V1> mapBoth(
      Function<? super L, ? extends V> mapper, Function<? super R, ? extends V1> mapper1) {
    return fork(mapper.apply(left()), mapper1.apply(right()));
  }

  default Tuple<L, R> mutable() {
    return new MutableTuple<>(left(), right());
  }

  default Tuple<L, R> immutable() {
    return new ImmutableTuple<>(left(), right());
  }
}
