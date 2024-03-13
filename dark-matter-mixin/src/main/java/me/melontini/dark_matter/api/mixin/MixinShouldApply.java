package me.melontini.dark_matter.api.mixin;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ApiStatus.Obsolete(since = "2.0.0")
public @interface MixinShouldApply {

    Mod[] mods() default {};

    String mcVersion() default "";

}
