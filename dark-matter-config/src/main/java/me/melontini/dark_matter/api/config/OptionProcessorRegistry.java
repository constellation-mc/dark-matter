package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Processor;

public interface OptionProcessorRegistry<T> {
    void register(String id, Processor<T> processor);
}
