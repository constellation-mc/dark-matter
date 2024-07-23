package me.melontini.dark_matter.impl.data.codecs;

import com.mojang.serialization.*;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class SafeOptionalCodec<A> extends MapCodec<Optional<A>> {
  private final String name;
  private final Codec<A> elementCodec;

  public SafeOptionalCodec(final String name, final Codec<A> elementCodec) {
    this.name = name;
    this.elementCodec = elementCodec;
  }

  @Override
  public <T> DataResult<Optional<A>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
    final T value = input.get(name);
    if (value == null) {
      return DataResult.success(Optional.empty());
    }
    return elementCodec.parse(ops, value).map(Optional::of);
  }

  @Override
  public <T> RecordBuilder<T> encode(
      final Optional<A> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
    if (input.isPresent()) {
      return prefix.add(name, elementCodec.encodeStart(ops, input.get()));
    }
    return prefix;
  }

  @Override
  public <T> Stream<T> keys(final DynamicOps<T> ops) {
    return Stream.of(ops.createString(name));
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final SafeOptionalCodec<?> that = (SafeOptionalCodec<?>) o;
    return Objects.equals(name, that.name) && Objects.equals(elementCodec, that.elementCodec);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, elementCodec);
  }

  @Override
  public String toString() {
    return "SafeOptionalCodec[" + name + ": " + elementCodec + ']';
  }
}
