package me.melontini.dark_matter.api.base.util.classes;

import java.util.concurrent.atomic.AtomicReference;

public class ThrowableStorage<T extends Throwable> {

    private final AtomicReference<T> throwable = new AtomicReference<>();

    public ThrowableStorage() {
        throwable.set(null);
    }

    public ThrowableStorage(T throwable) {
        this.throwable.set(throwable);
    }

    public static <T extends Throwable> ThrowableStorage<T> of(T throwable) {
        return new ThrowableStorage<>(throwable);
    }

    public static <T extends Throwable> ThrowableStorage<T> of() {
        return new ThrowableStorage<>();
    }

    public void set(T throwable) {
        this.throwable.set(throwable);
    }

    public T get() {
        return throwable.get();
    }

    public void tryThrow() throws T {
        if (throwable.get() != null) {
            throw throwable.get();
        }
    }
}
