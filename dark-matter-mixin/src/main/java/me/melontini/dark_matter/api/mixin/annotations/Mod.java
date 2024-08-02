package me.melontini.dark_matter.api.mixin.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mod {

  String value();

  String version() default "*";

  State state() default State.LOADED;

  enum State {
    LOADED,
    NOT_LOADED
  }
}
