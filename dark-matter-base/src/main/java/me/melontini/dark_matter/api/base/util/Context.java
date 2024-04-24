package me.melontini.dark_matter.api.base.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public interface Context {

    <T> Optional<T> get(Key<T> key);
    void forEach(BiConsumer<Key<?>, Object> consumer);
    default <T> T orThrow(Key<T> key) {
        return get(key).orElseThrow(() -> new IllegalStateException("Missing required context '%s'!".formatted(key)));
    }

    static <T> Key<T> key(String id) {
        return new Key<>(id);
    }
    record Key<T>(String id) { }

    static Context of() {
        return Empty.INSTANCE;
    }

    static Context of(Map<Key<?>, Object> map) {
        return new Context() {
            private final Map<Key<?>, Object> ctx = Collections.unmodifiableMap(map);
            @Override
            public <T> Optional<T> get(Key<T> key) {
                return Optional.ofNullable((T) ctx.get(key));
            }

            @Override
            public void forEach(BiConsumer<Key<?>, Object> consumer) {
                ctx.forEach(consumer);
            }

            @Override
            public String toString() {
                return ctx.toString();
            }
        };
    }

    static Builder builder() {
        return new Builder();
    }

    class Builder {
        private final Map<Key<?>, Object> map = Collections.synchronizedMap(new HashMap<>());
        private Builder() {}

        public <T> Builder put(Key<T> key, T value) {
            map.put(key, value);
            return this;
        }
        public Context build() {
            return Context.of(map);
        }
    }

    enum Empty implements Context {
        INSTANCE;

        @Override
        public <T> Optional<T> get(Key<T> key) {
            return Optional.empty();
        }

        @Override
        public void forEach(BiConsumer<Key<?>, Object> consumer) {}
    }
}
