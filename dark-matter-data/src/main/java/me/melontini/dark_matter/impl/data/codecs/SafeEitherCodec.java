// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.
package me.melontini.dark_matter.impl.data.codecs;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Objects;

public final class SafeEitherCodec<F, S> implements Codec<Either<F, S>> {
    private final Codec<F> first;
    private final Codec<S> second;

    public SafeEitherCodec(final Codec<F> first, final Codec<S> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public <T> DataResult<Pair<Either<F, S>, T>> decode(final DynamicOps<T> ops, final T input) {
        final DataResult<Pair<Either<F, S>, T>> firstRead = first.decode(ops, input).map(vo -> vo.mapFirst(Either::left));
        if (firstRead.result().isPresent()) return firstRead;
        final DataResult<Pair<Either<F, S>, T>> secondRead = second.decode(ops, input).map(vo -> vo.mapFirst(Either::right));
        if (secondRead.result().isPresent()) return secondRead;
        return secondRead.mapError(string -> "first: " + firstRead.error().orElseThrow().message() + "\n\tsecond:" + string);
    }

    @Override
    public <T> DataResult<T> encode(final Either<F, S> input, final DynamicOps<T> ops, final T prefix) {
        return input.map(
            value1 -> first.encode(value1, ops, prefix),
            value2 -> second.encode(value2, ops, prefix)
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SafeEitherCodec<?, ?> eitherCodec = ((SafeEitherCodec<?, ?>) o);
        return Objects.equals(first, eitherCodec.first) && Objects.equals(second, eitherCodec.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "SafeEitherCodec[" + first + ", " + second + ']';
    }
}
