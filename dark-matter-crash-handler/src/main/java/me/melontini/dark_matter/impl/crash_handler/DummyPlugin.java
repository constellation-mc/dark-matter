package me.melontini.dark_matter.impl.crash_handler;

import me.melontini.dark_matter.api.base.util.mixin.ExtendablePlugin;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.spongepowered.asm.mixin.Mixins;

public class DummyPlugin extends ExtendablePlugin {

    static {
        try {
            Mixins.registerErrorHandlerClass(MixinErrorHandler.class.getName());
        } catch (Throwable e) {
            DarkMatterLog.error("Failed to register mixin error handler!", e);
        }
    }
}
