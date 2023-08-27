package me.melontini.dark_matter.api.base.util.mixin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * By design, {@code public static} members are not allowed in a mixin class.
 * But, what if you really want to expose a method or a field?
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Publicize {
}
