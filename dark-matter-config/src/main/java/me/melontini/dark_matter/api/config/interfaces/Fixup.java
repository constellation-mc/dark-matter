package me.melontini.dark_matter.api.config.interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@FunctionalInterface
public interface Fixup {
    boolean fixup(JsonObject config, JsonElement value, String key);
}
