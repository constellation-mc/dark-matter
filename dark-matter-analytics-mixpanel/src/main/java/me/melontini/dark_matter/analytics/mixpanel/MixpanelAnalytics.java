package me.melontini.dark_matter.analytics.mixpanel;

import com.google.gson.JsonObject;
import me.melontini.dark_matter.DarkMatterLog;
import me.melontini.dark_matter.analytics.Analytics;
import me.melontini.dark_matter.analytics.MessageHandler;
import me.melontini.dark_matter.analytics.Prop;
import me.melontini.dark_matter.analytics.mixpanel.api.MixpanelAPI;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * The MixpanelAnalytics class provides methods for sending analytics data to Mixpanel, a cloud-based analytics platform.
 */
public class MixpanelAnalytics {
    private static final Map<String, Handler> MESSAGE_HANDLERS = new ConcurrentHashMap<>();

    /**
     * Initializes a Handler instance for the provided token and stores it in the MESSAGE_HANDLERS map.
     *
     * @param token The Mixpanel project token to use for sending analytics data.
     * @param eu    A boolean value indicating whether to send analytics data to the Mixpanel EU server or not.
     * @return A MixpanelHandler instance for the provided token.
     */
    public static Handler init(String token, boolean eu) {
        return MESSAGE_HANDLERS.computeIfAbsent(token, k -> new Handler(new MixpanelAPI(eu, token)));
    }

    /**
     * Attaches properties to a JSONObject.
     *
     * @param object The JSONObject to attach properties to.
     * @param props  The properties to attach to the JSONObject.
     * @return The modified JSONObject.
     */
    @Contract("_, _ -> param1")
    public static JsonObject attachProps(JsonObject object, Prop @NotNull ... props) {
        for (Prop prop : props) {
            object.addProperty(getPropName(prop), prop.get());
        }
        return object;
    }

    public static class Handler extends MessageHandler<MessageProvider> {
        private final MixpanelAPI mixpanel;

        public Handler(MixpanelAPI api) {
            this.mixpanel = api;
        }

        protected void sendInternal(MessageProvider consumer, boolean wait, boolean errors) {
            if (!Analytics.isEnabled() && !Analytics.handleCrashes()) return;
            Future<?> future = EXECUTOR.submit(() -> {
                try {
                    consumer.consume(this.mixpanel);
                } catch (Exception e) {
                    if (errors) DarkMatterLog.error("Could not send analytics message", e);
                }
            });
            if (wait) {
                try {
                    future.get();
                } catch (Exception e) {
                    if (errors) DarkMatterLog.error("Could not wait for analytics message", e);
                }
            }
        }

        @Override
        public String getPropName(Prop prop) {
            return MixpanelAnalytics.getPropName(prop);
        }
    }

    public interface MessageProvider {
        void consume(MixpanelAPI mixpanel);
    }

    public static String getPropName(Prop prop) {
        return switch (prop) {
            case OS -> "$os";
            case TIMEZONE -> "$timezone";
            case COUNTRY_CODE -> "mp_country_code";
            default -> prop.name().toLowerCase();
        };
    }
}
