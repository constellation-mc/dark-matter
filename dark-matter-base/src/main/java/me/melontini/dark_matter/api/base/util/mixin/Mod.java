package me.melontini.dark_matter.api.base.util.mixin;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
@ApiStatus.Obsolete(since = "2.0.0")
public @interface Mod {
    String value();

    Mode mode() default Mode.LOADED;

    enum Mode {
        LOADED,
        NOT_LOADED
    }

}
