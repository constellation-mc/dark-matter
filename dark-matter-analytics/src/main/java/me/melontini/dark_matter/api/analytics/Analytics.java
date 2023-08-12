package me.melontini.dark_matter.api.analytics;

import me.melontini.dark_matter.impl.analytics.AnalyticsInternals;

import java.util.UUID;


public class Analytics {

    private Analytics() {
        throw new UnsupportedOperationException();
    }

    public static UUID getUUID() {
        return AnalyticsInternals.getUUID();
    }

    public static String getUUIDString() {
        return AnalyticsInternals.getUUIDString();
    }

    public static boolean isEnabled() {
        return AnalyticsInternals.isEnabled();
    }

    public static boolean handleCrashes() {
        return AnalyticsInternals.handleCrashes();
    }

}
