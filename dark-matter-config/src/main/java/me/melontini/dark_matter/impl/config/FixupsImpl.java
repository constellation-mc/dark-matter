package me.melontini.dark_matter.impl.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.config.FixupsBuilder;
import me.melontini.dark_matter.api.config.interfaces.Fixup;
import me.melontini.dark_matter.api.config.interfaces.Fixups;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FixupsImpl implements Fixups, FixupsBuilder {

    private final Map<String[], Set<Fixup>> fixups = new HashMap<>();

    @Override
    public boolean isEmpty() {
        return fixups.isEmpty();
    }

    @Override
    public JsonObject fixup(JsonObject config) {
        for (Map.Entry<String[], Set<Fixup>> entry : fixups.entrySet()) {
            JsonElement e = config;
            for (String key : entry.getKey())
                if (e instanceof JsonObject o) e = o.get(key);

            final JsonElement element = e;
            entry.getValue().forEach(fixup -> {
                if (fixup.fixup(config, element, entry.getKey()))
                    DarkMatterLog.debug("Fixed-up config entry {}", StringUtils.join(".", entry.getKey()));
            });
        }
        return config;
    }

    @Override
    public FixupsBuilder add(String key, Fixup fixup) {
        fixups.computeIfAbsent(key.split("\\."), k -> new HashSet<>()).add(fixup);
        return this;
    }

    @Override
    public Fixups build() {
        return this;
    }
}
