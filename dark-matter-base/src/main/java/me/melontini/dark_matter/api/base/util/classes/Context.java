package me.melontini.dark_matter.api.base.util.classes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface Context {

    <T> Optional<T> get(Class<T> cls, String key);
    default <T> T orThrow(Class<T> cls, String key) {
        return get(cls, key).orElseThrow(() -> new IllegalStateException("Missing required context '%s'!".formatted(key)));
    }

    static Context of() {
        return new Context() {
            @Override
            public <T> Optional<T> get(Class<T> cls, String key) {
                return Optional.empty();
            }

            @Override
            public String toString() {
                return Collections.emptyMap().toString();
            }
        };
    }

    static Context of(Map<String, Object> map) {
        return new Context() {
            private final Map<String, Object> ctx = Collections.unmodifiableMap(map);
            @Override
            public <T> Optional<T> get(Class<T> cls, String key) {
                return Optional.ofNullable(cls.cast(ctx.get(key)));
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
        private final Map<String, Object> map = Collections.synchronizedMap(new HashMap<>());
        private Builder() {}

        public Builder put(String key, Object value) {
            map.put(key, value);
            return this;
        }
        public Context build() {
            return Context.of(map);
        }
    }
}
