package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Processor;
import me.melontini.dark_matter.api.config.interfaces.TextEntry;

import java.util.function.Function;

public interface OptionProcessorRegistry<T> {
    void register(String id, Processor<T> processor);

    void register(String id, Processor<T> processor, Function<TextEntry.InfoHolder<T>, TextEntry> reason);
}
