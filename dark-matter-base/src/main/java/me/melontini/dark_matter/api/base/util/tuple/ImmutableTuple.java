package me.melontini.dark_matter.api.base.util.tuple;

public record ImmutableTuple<L, R>(L left, R right) implements Tuple<L, R> {

  public static <L, R> ImmutableTuple<L, R> of(L left, R right) {
    return new ImmutableTuple<>(left, right);
  }

  @Override
  public Tuple<L, R> left(L left) {
    return fork(left, right());
  }

  @Override
  public Tuple<L, R> right(R right) {
    return fork(left(), right);
  }

  @Override
  public <L1, R1> Tuple<L1, R1> fork(L1 left, R1 right) {
    return new ImmutableTuple<>(left, right);
  }

  @Override
  public Tuple<L, R> immutable() {
    return this;
  }

  @Override
  public String toString() {
    return "Tuple{" + "left=" + left + ", right=" + right + '}';
  }
}
