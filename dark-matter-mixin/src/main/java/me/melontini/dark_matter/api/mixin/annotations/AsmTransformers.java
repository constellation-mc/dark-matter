package me.melontini.dark_matter.api.mixin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.melontini.dark_matter.api.mixin.IAsmTransformer;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmTransformers {

  Class<? extends IAsmTransformer>[] value();
}
