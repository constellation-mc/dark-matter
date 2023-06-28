package me.melontini.dark_matter.analytics.mixpanel;

import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import me.melontini.dark_matter.DarkMatterLog;
import me.melontini.dark_matter.analytics.Analytics;
import me.melontini.dark_matter.analytics.MessageHandler;
import me.melontini.dark_matter.analytics.Prop;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * The MixpanelAnalytics class provides methods for sending analytics data to Mixpanel, a cloud-based analytics platform.
 */
public class MixpanelAnalytics {
    private static final Map<String, Handler> MESSAGE_HANDLERS = new ConcurrentHashMap<>();
    private static final MixpanelAPI MIXPANEL = new MixpanelAPI();
    private static final MixpanelAPI MIXPANEL_EU = new MixpanelAPI("https://api-eu.mixpanel.com/track", "https://api-eu.mixpanel.com/engage", "https://api-eu.mixpanel.com/groups");

    /**
     * Initializes a Handler instance for the provided token and stores it in the MESSAGE_HANDLERS map.
     *
     * @param token The Mixpanel project token to use for sending analytics data.
     * @param eu    A boolean value indicating whether to send analytics data to the Mixpanel EU server or not.
     * @return A MixpanelHandler instance for the provided token.
     */
    public static Handler init(String token, boolean eu) {
        return MESSAGE_HANDLERS.computeIfAbsent(token, k -> new Handler(eu, new MessageBuilder(token)));
    }

    /**
     * Attaches properties to a JSONObject.
     *
     * @param object The JSONObject to attach properties to.
     * @param props  The properties to attach to the JSONObject.
     * @return The modified JSONObject.
     */
    @Contract("_, _ -> param1")
    public static JSONObject attachProps(JSONObject object, Prop @NotNull ... props) {
        for (Prop prop : props) {
            object.put(getPropName(prop), prop.get());
        }
        return object;
    }

    public static class Handler extends MessageHandler<MessageProvider> {
        private final MessageBuilder messageBuilder;
        private final MixpanelAPI mixpanel;

        public Handler(boolean eu, MessageBuilder messageBuilder) {
            this.messageBuilder = messageBuilder;
            this.mixpanel = eu ? MIXPANEL_EU : MIXPANEL;
        }

        protected void sendInternal(MessageProvider consumer, boolean wait, boolean errors) {
            if (!Analytics.isEnabled()) return;
            Future<?> future = EXECUTOR.submit(() -> {
                try {
                    JSONObject message = consumer.consume(messageBuilder);
                    if (message != null) {
                        mixpanel.sendMessage(message);
                    }
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
        JSONObject consume(MessageBuilder messageBuilder);
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
