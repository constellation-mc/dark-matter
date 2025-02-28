package me.melontini.dark_matter.api.base.util.tuple;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public final class MutableTuple<L, R> implements Tuple<L, R> {

  public static <L, R> MutableTuple<L, R> of(L left, R right) {
    return new MutableTuple<>(left, right);
  }

  private L left;
  private R right;

  public MutableTuple(L left, R right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public <L1, R1> Tuple<L1, R1> fork(L1 left, R1 right) {
    return new MutableTuple<>(left, right);
  }

  @Override
  public Tuple<L, R> mutable() {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MutableTuple<?, ?> that = (MutableTuple<?, ?>) o;
    return Objects.equals(left, that.left) && Objects.equals(right, that.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }

  @Override
  public String toString() {
    return "MutableTuple{" + "left=" + left + ", right=" + right + '}';
  }
}
