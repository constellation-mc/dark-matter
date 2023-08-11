package me.melontini.dark_matter.api.analytics.mixpanel;

import com.google.gson.JsonObject;
import me.melontini.dark_matter.impl.analytics.mixpanel.MixpanelAPI;

import java.util.List;
import java.util.Map;

/**
 * Basic API to interact with Mixpanel.
 * <p>
 * Only features basic functionality.
 */
public final class Mixpanel {

    private final MixpanelAPI api;

    public Mixpanel(boolean eu, String projectToken) {
        this.api = new MixpanelAPI(eu, projectToken);
    }

    public void trackEvent(String userID, String eventName, JsonObject props) {
        this.api.trackEvent(userID, eventName, props);
    }

    public void set(String userID, JsonObject props) {
        this.api.set(userID, props);
    }

    public void setOnce(String userID, JsonObject props) {
        this.api.setOnce(userID, props);
    }

    public void add(String userID, Map<String, Long> increment) {
        this.add(userID, increment);
    }

    public void unset(String userID, String... props) {
        this.api.unset(userID, props);
    }

    public void unset(String userID, List<String> props) {
        this.api.unset(userID, props);
    }

    public void delete(String userID) {
        this.api.delete(userID);
    }
}
