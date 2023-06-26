package me.melontini.dark_matter.util.mixin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mod {
    String value();

    Mode mode() default Mode.LOADED;

    enum Mode {
        LOADED,
        NOT_LOADED
    }
}
