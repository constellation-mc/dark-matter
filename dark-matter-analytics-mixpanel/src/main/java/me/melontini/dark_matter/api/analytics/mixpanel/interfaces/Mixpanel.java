package me.melontini.dark_matter.api.analytics.mixpanel.interfaces;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * Basic API to interact with Mixpanel.
 * <p>
 * Only features basic functionality.
 */
public interface Mixpanel {

    void trackEvent(String userID, String eventName, JsonObject props);

    void set(String userID, JsonObject props);

    void setOnce(String userID, JsonObject props);

    void add(String userID, Map<String, Long> increment);

    void unset(String userID, String... props);

    void unset(String userID, List<String> props);

    void delete(String userID);

}
