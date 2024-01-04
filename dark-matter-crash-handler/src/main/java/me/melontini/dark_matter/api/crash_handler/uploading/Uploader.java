package me.melontini.dark_matter.api.crash_handler.uploading;

import me.melontini.dark_matter.api.crash_handler.Prop;
import me.melontini.dark_matter.impl.crash_handler.uploading.Config;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The base interface for all uploaders. {@link Mixpanel} or {@link McLogs}
 * @param <R> The type of the response from the uploader. Can be Void
 * @param <C> The type of the context that is passed to the uploader. Must be a {@link Record} subclass.
 */
public interface Uploader<R, C extends Record> {

    ExecutorService SERVICE = Executors.newSingleThreadExecutor(r -> new Thread(r, "Dark-Matter-Uploader-Thread"));
    UUID CRASH_UUID = UUID.fromString("be4db047-16df-4e41-9121-f1e87618ddea");

    /**
     * Uploads the crash report to the impl server.
     * @param context The context to upload with.
     * @return A completed future with the response or a failed future if the upload failed.
     */
    CompletableFuture<R> upload(C context);

    default String getPropName(Prop prop) {
        return prop.name().toLowerCase();
    }

    static boolean enabled() {
        return Config.enabled();
    }

    static RuntimeException uploadDisabledException() {
        return new RuntimeException("Uploading is disabled by user config.");
    }
}
