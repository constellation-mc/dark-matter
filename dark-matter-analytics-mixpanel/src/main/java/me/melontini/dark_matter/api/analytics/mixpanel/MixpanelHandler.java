package me.melontini.dark_matter.api.analytics.mixpanel;

import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.api.analytics.MessageHandler;
import me.melontini.dark_matter.api.analytics.Prop;
import me.melontini.dark_matter.impl.base.DarkMatterLog;

import java.util.concurrent.Future;

public class MixpanelHandler extends MessageHandler<MixpanelAnalytics.MessageProvider> {
    private final Mixpanel mixpanel;

    public MixpanelHandler(Mixpanel api) {
        this.mixpanel = api;
    }

    protected void sendInternal(MixpanelAnalytics.MessageProvider consumer, boolean wait, boolean errors) {
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
