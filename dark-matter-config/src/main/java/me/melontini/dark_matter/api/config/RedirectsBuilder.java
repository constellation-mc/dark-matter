package me.melontini.dark_matter.api.config;

import me.melontini.dark_matter.api.config.interfaces.Redirects;
import me.melontini.dark_matter.impl.config.RedirectsBuilderImpl;


public interface RedirectsBuilder {

    static RedirectsBuilder create() {
        return new RedirectsBuilderImpl();
    }

    RedirectsBuilder add(String from, String to);

    Redirects build();
}
