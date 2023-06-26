package me.melontini.dark_matter.dark_matter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class defines a handler for sending analytics messages to a backend.
 *
 * @param <T> The type of message consumer that this handler will send messages to.
 */
public abstract class MessageHandler<T> {
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> new Thread(r, "Dark Matter analytics thread"));

    public final void send(T consumer, boolean wait, boolean errors) {
        if (Analytics.isEnabled()) send0(consumer, wait, errors);
    }

    public final void send(T consumer, boolean wait) {
        if (Analytics.isEnabled()) send0(consumer, wait, false);
    }

    public final void send(T consumer) {
        if (Analytics.isEnabled()) send0(consumer, false, false);
    }

    protected abstract void send0(T consumer, boolean wait, boolean errors);

    public String getPropName(Prop prop) {
        return prop.name().toLowerCase();
    }
}
