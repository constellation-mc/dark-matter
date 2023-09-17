package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Fixup;
import me.melontini.dark_matter.api.config.interfaces.Fixups;
import me.melontini.dark_matter.impl.config.FixupBuilderImpl;

public interface FixupsBuilder {

    static FixupsBuilder create() {
        return new FixupBuilderImpl();
    }

    FixupsBuilder add(String key, Fixup fixup);

    Fixups build();
}
