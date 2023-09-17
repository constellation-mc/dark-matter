package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.config.FixupsBuilder;
import me.melontini.dark_matter.api.config.interfaces.Fixup;
import me.melontini.dark_matter.api.config.interfaces.Fixups;

public class FixupBuilderImpl implements FixupsBuilder {

    private final FixupsImpl fixups = new FixupsImpl();

    public FixupBuilderImpl add(String key, Fixup fixup) {
        fixups.addFixup(key, fixup);
        return this;
    }

    public Fixups build() {
        return fixups;
    }
}
