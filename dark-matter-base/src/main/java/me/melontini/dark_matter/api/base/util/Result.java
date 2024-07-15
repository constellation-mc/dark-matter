package me.melontini.dark_matter.api.base.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public abstract sealed class Result<V, E> permits Result.Err, Result.Ok {

    @Contract("_ -> new")
    public static <V, E> @NotNull Result<V, E> error(@Nullable E error) {
        return error == null ? empty() : new Err<>(error);
    }

    public static <V, E> @NotNull Result<V, E> ok(@Nullable V value) {
        return value == null ? empty() : new Ok<>(value);
    }

    public static <V, E> @NotNull Result<V, E> empty() {
        return (Result<V, E>) Ok.EMPTY;
    }

    public abstract <A> @NotNull Result<A, E> mapVal(Function<? super V, ? extends A> mapper);
    public abstract <X> @NotNull Result<V, X> mapErr(Function<? super E, ? extends X> mapper);
    public <A, X> @NotNull Result<A, X> map(Function<? super V, ? extends A> valMapper, Function<? super E, ? extends X> errMapper) {
        return this.<A>mapVal(valMapper).mapErr(errMapper);
    }

    public abstract <A> @NotNull Result<A, E> flatmapVal(Function<? super V, ? extends Result<? extends A, E>> mapper);
    public abstract <X> @NotNull Result<V, X> flatmapErr(Function<? super E, ? extends Result<V, ? extends X>> mapper);

    public abstract @NotNull Result<V, E> filterVal(Predicate<? super V> predicate);
    public abstract @NotNull Result<V, E> filterErr(Predicate<? super E> predicate);
    public @NotNull Result<V, E> filter(Predicate<? super V> valPredicate, Predicate<? super E> errPredicate) {
        return this.filterVal(valPredicate).filterErr(errPredicate);
    }

    public abstract Optional<V> value();
    public abstract Optional<E> error();

    @Value @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    static class Ok<V, E> extends Result<V, E> {
        public static Ok<?, ?> EMPTY = new Ok<>(null);

        V value;

        @Override
        public @NotNull <A> Result<A, E> mapVal(Function<? super V, ? extends A> mapper) {
            if (value == null) return (Result<A, E>) this;
            return ok(mapper.apply(value));
        }

        @Override
        public @NotNull <X> Result<V, X> mapErr(Function<? super E, ? extends X> mapper) {
            return (Result<V, X>) this;
        }

        @Override
        public @NotNull <A> Result<A, E> flatmapVal(Function<? super V, ? extends Result<? extends A, E>> mapper) {
            if (value == null) return (Result<A, E>) this;
            return MakeSure.notNull((Result<A, E>) mapper.apply(value));
        }

        @Override
        public @NotNull <X> Result<V, X> flatmapErr(Function<? super E, ? extends Result<V, ? extends X>> mapper) {
            return (Result<V, X>) this;
        }

        @Override
        public @NotNull Result<V, E> filterVal(Predicate<? super V> predicate) {
            if (value == null) return this;
            return predicate.test(value) ? this : empty();
        }

        @Override
        public @NotNull Result<V, E> filterErr(Predicate<? super E> predicate) {
            return this;
        }

        @Override
        public Optional<V> value() {
            return Optional.ofNullable(value);
        }

        @Override
        public Optional<E> error() {
            return Optional.empty();
        }
    }

    @Value @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(callSuper = false)
    static class Err<V, E> extends Result<V, E> {
        E error;

        @Override
        public @NotNull <A> Result<A, E> mapVal(Function<? super V, ? extends A> mapper) {
            return (Result<A, E>) this;
        }

        @Override
        public @NotNull <X> Result<V, X> mapErr(Function<? super E, ? extends X> mapper) {
            if (error == null) return (Result<V, X>) this;
            return error(mapper.apply(error));
        }

        @Override
        public @NotNull <A> Result<A, E> flatmapVal(Function<? super V, ? extends Result<? extends A, E>> mapper) {
            return (Result<A, E>) this;
        }

        @Override
        public @NotNull <X> Result<V, X> flatmapErr(Function<? super E, ? extends Result<V, ? extends X>> mapper) {
            if (error == null) return (Result<V, X>) this;
            return MakeSure.notNull((Result<V, X>) mapper.apply(error));
        }

        @Override
        public @NotNull Result<V, E> filterVal(Predicate<? super V> predicate) {
            return this;
        }

        @Override
        public @NotNull Result<V, E> filterErr(Predicate<? super E> predicate) {
            if (error == null) return this;
            return predicate.test(error) ? this : empty();
        }

        @Override
        public Optional<V> value() {
            return Optional.empty();
        }

        @Override
        public Optional<E> error() {
            return Optional.ofNullable(error);
        }
    }
}
