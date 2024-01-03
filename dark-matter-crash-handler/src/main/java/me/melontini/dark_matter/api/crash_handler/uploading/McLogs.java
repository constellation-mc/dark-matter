package me.melontini.dark_matter.api.crash_handler.uploading;

import me.melontini.dark_matter.impl.crash_handler.uploading.McLogsImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface McLogs extends Uploader<String, McLogs.Context> {

    static McLogs get() {
        return McLogsImpl.INSTANCE;
    }

    /**
     * Uploads the log to <a href="https://mclo.gs">mclo.gs</a>
     * @param context The log to upload. Must not be null or empty.
     * @return The url of the uploaded log or null if the upload failed or upload is disabled in the config.
     */
    @Override
    @Nullable String upload(Context context);

    record Context(@NotNull String log) { }
}
