package me.melontini.dark_matter.api.crash_handler;

import java.util.function.Supplier;

public interface Prop extends Supplier<String> {
    String name();
    @Override
    String get();
}
