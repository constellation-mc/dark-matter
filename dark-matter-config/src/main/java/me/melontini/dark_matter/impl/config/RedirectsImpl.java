package me.melontini.dark_matter.impl.config;

import me.melontini.dark_matter.api.config.interfaces.Redirects;

import java.util.HashMap;
import java.util.Map;

public class RedirectsImpl implements Redirects {

    private final Map<String, String> redirects = new HashMap<>();

    void addRedirect(String from, String to) {
        redirects.put(from, to);
    }

    @Override
    public String redirect(String from) {
        return redirects.getOrDefault(from, from);
    }
}
