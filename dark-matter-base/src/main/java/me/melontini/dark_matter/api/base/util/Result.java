package me.melontini.dark_matter.api.base.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public record Result<V, E>(Optional<V> value, Optional<E> error) {

    private static final Result<?, ?> EMPTY = new Result<>(Optional.empty(), Optional.empty());
    public static <V, E> @NotNull Result<V, E> of(@Nullable V value, @Nullable E error) {
        return value == null && error == null ? empty() : new Result<>(Optional.ofNullable(value), Optional.ofNullable(error));
    }

    @Contract("_ -> new")
    public static <V, E> @NotNull Result<V, E> error(@Nullable E error) {
        return new Result<>(Optional.empty(), Optional.ofNullable(error));
    }

    public static <V, E> @NotNull Result<V, E> ok(@Nullable V value) {
        return value == null ? empty() : new Result<>(Optional.of(value), Optional.empty());
    }

    public static <V, E> @NotNull Result<V, E> empty() {
        return (Result<V, E>) EMPTY;
    }

    public <A> @NotNull Result<A, E> mapVal(Function<? super V, ? extends A> mapper) {
        if (value().isEmpty()) return (Result<A, E>) this;
        return new Result<>(value().map(mapper), error());
    }

    public <X> @NotNull Result<V, X> mapErr(Function<? super E, ? extends X> mapper) {
        if (error().isEmpty()) return (Result<V, X>) this;
        return new Result<>(value(), error().map(mapper));
    }

    public <A, X> @NotNull Result<A, X> map(Function<? super V, ? extends A> valMapper, Function<? super E, ? extends X> errMapper) {
        if (value().isEmpty() && error().isEmpty()) return empty();
        return new Result<>(value().map(valMapper), error().map(errMapper));
    }

    public <A> @NotNull Result<A, E> flatmapVal(Function<? super V, ? extends Result<? extends A, E>> mapper) {
        if (value().isEmpty()) return (Result<A, E>) this;
        return MakeSure.notNull((Result<A, E>) mapper.apply(value().get()));
    }

    public <X> @NotNull Result<V, X> flatmapErr(Function<? super E, ? extends Result<V, ? extends X>> mapper) {
        if (error().isEmpty()) return (Result<V, X>) this;
        return MakeSure.notNull((Result<V, X>) mapper.apply(error().get()));
    }

    public @NotNull Result<V, E> filterVal(Predicate<? super V> predicate) {
        if (value().isEmpty()) return this;
        return new Result<>(value().filter(predicate), error());
    }

    public @NotNull Result<V, E> filterErr(Predicate<? super E> predicate) {
        if (error().isEmpty()) return this;
        return new Result<>(value(), error().filter(predicate));
    }

    public @NotNull Result<V, E> filter(Predicate<? super V> valPredicate, Predicate<? super E> errPredicate) {
        if (value().isEmpty() && error().isEmpty()) return empty();
        return new Result<>(value().filter(valPredicate), error().filter(errPredicate));
    }

    @ApiStatus.Experimental
    public @NotNull Result<V, E> intern() {
        if (this == EMPTY) return this;
        return error().isEmpty() && value().isEmpty() ? empty() : this;
    }
}
