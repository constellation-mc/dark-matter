package me.melontini.dark_matter.impl.analytics.mixpanel;

import me.melontini.dark_matter.api.analytics.mixpanel.MixpanelHandler;
import me.melontini.dark_matter.api.base.util.MakeSure;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class MixpanelAnalyticsInternals {
    private static final Map<String, MixpanelHandler> MESSAGE_HANDLERS = new ConcurrentHashMap<>();

    public static MixpanelHandler init(String token, boolean eu) {
        MakeSure.notEmpty(token, "Invalid token provided! (null/empty)");
        return MESSAGE_HANDLERS.computeIfAbsent(token, k -> new MixpanelHandler(new MixpanelAPI(eu, token)));
    }

}
