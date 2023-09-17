package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.config.RedirectsBuilder;
import me.melontini.dark_matter.api.config.interfaces.Redirects;

public class RedirectsBuilderImpl implements RedirectsBuilder {

    private final RedirectsImpl redirects = new RedirectsImpl();

    @Override
    public RedirectsBuilderImpl add(String from, String to) {
        this.redirects.addRedirect(from, to);
        return this;
    }

    @Override
    public Redirects build() {
        return redirects;
    }
}
