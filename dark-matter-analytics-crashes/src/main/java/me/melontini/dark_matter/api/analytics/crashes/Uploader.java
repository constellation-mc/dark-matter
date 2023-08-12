package me.melontini.dark_matter.api.analytics.crashes;

import me.melontini.dark_matter.impl.analytics.crashes.UploaderInternals;

public class Uploader {

    private Uploader() {
        throw new UnsupportedOperationException();
    }

    public static String uploadToMclo_gs(String log) {
        return UploaderInternals.uploadToMclo_gs(log);
    }

}
