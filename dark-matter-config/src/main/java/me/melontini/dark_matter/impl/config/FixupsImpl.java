package me.melontini.dark_matter.impl.config;

import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.config.interfaces.Fixup;
import me.melontini.dark_matter.api.config.interfaces.Fixups;
import me.melontini.dark_matter.impl.base.DarkMatterLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FixupsImpl implements Fixups {

    private final Map<String, Set<Fixup>> fixups = new HashMap<>();

    void addFixup(String key, Fixup fixup) {
        fixups.computeIfAbsent(key, k -> new HashSet<>()).add(fixup);
    }

    public boolean isEmpty() {
        return fixups.isEmpty();
    }

    @Override
    public JsonObject fixup(JsonObject config) {
        for (Map.Entry<String, Set<Fixup>> entry : fixups.entrySet()) {
            if (config.has(entry.getKey())) {
                entry.getValue().forEach(fixup -> {
                    if (fixup.fixup(config, config.get(entry.getKey()), entry.getKey()))
                        DarkMatterLog.debug("Fixed-up config entry {}", entry.getKey());
                });
            }
        }
        return config;
    }
}
