package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Processor;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface OptionProcessorRegistry<T> {
    void register(String id, Processor<T> processor, ModContainer mod);

    void register(String id, Processor<T> processor, ModContainer mod, ConfigBuilder.DefaultReason<T> reason);
}
