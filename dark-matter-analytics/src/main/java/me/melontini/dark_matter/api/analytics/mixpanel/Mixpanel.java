package me.melontini.dark_matter.api.analytics.mixpanel;

import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.api.analytics.Prop;
import me.melontini.dark_matter.impl.analytics.mixpanel.MixpanelAPI;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Basic API to interact with Mixpanel.
 * <p>
 * Only features basic functionality.
 */
public interface Mixpanel extends Analytics {

    static Mixpanel get(ModContainer mod, String token, boolean eu) {
        return new MixpanelAPI(eu, token, mod);
    }

    void trackEvent(String userID, String eventName, JsonObject props);

    void set(String userID, JsonObject props);

    void setOnce(String userID, JsonObject props);

    void add(String userID, Map<String, Long> increment);

    void unset(String userID, String... props);

    void unset(String userID, List<String> props);

    void delete(String userID);

    /**
     * Attaches properties to a JSONObject.
     *
     * @param object The JSONObject to attach properties to.
     * @param props  The properties to attach to the JSONObject.
     * @return The modified JSONObject.
     */
    @Contract("_, _ -> param1")
    static JsonObject attachProps(JsonObject object, Prop @NotNull ... props) {
        for (Prop prop : props) {
            object.addProperty(getPropName(prop), prop.get());
        }
        return object;
    }

    static String getPropName(Prop prop) {
        if (prop == Prop.OS) {
            return "$os";
        }
        return prop.name().toLowerCase();
    }
}
