package me.melontini.dark_matter.api.analytics;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * This class defines a handler for sending analytics messages to a backend.
 *
 * @param <T> The type of message consumer that this handler will send messages to.
 */
public abstract class MessageHandler<T extends Analytics> {

    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> new Thread(r, "Analytics-Thread"));

    public static <T extends Analytics> MessageHandler<T> create(T analytics) {
        return new MessageHandler<>(analytics) {
            @Override
            protected void sendInternal(Consumer<T> consumer, boolean wait, boolean errors) {
                if (!Analytics.enabled()) return;
                if (wait) {
                    wrap(() -> consumer.accept(this.analytics), errors);
                } else {
                    EXECUTOR.submit(() -> wrap(() -> consumer.accept(this.analytics), errors));
                }
            }
        };
    }

    protected final T analytics;

    private static void wrap(Runnable r, boolean errors) {
        try {
            r.run();
        } catch (Exception e) {
            if (errors) DarkMatterLog.error("Could not send analytics message", e);
        }
    }

    public MessageHandler(T analytics) {
        MakeSure.notNull(analytics, "null analytics provided");
        this.analytics = analytics;
    }

    public final void send(Consumer<T> consumer, boolean wait, boolean errors) {
        MakeSure.notNull(consumer, "null consumer provided");
        if (Analytics.enabled()) sendInternal(consumer, wait, errors);
    }

    public final void send(Consumer<T> consumer, boolean wait) {
        MakeSure.notNull(consumer, "null consumer provided");
        if (Analytics.enabled()) sendInternal(consumer, wait, false);
    }

    public final void send(Consumer<T> consumer) {
        MakeSure.notNull(consumer, "null consumer provided");
        if (Analytics.enabled()) sendInternal(consumer, false, false);
    }

    @ApiStatus.OverrideOnly
    protected abstract void sendInternal(Consumer<T> consumer, boolean wait, boolean errors);

    public String getPropName(Prop prop) {
        return prop.name().toLowerCase();
    }
}
